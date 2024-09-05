package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FirebaseNotificationService {

    private static final Logger LOGGER = Logger.getLogger(FirebaseNotificationService.class.getName());
    public final Map<Long, LocalDateTime> notedAtStorage = new HashMap<>();
    public final Map<Long, Integer> employeeCountMap = new HashMap<>();
    private final EmployeeService EMPLOYEE_SERVICE;
    private final FirebaseDatabase FIREBASE_DATABASE;
    private final AnnouncementService ANNOUNCEMENT_SERVICE;

    public Map<Long, LocalDateTime> getNotedAtStorage() {
        return notedAtStorage;
    }

    public void markAsRead(String notificationId, String userId) {
        DatabaseReference ref = FIREBASE_DATABASE.getReference("notifications/" + userId + "/" + notificationId);
        ref.child("isRead").setValueAsync(true);
    }

    public void markAllAsRead(String userId) {
        DatabaseReference ref = FIREBASE_DATABASE.getReference("notifications/" + userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                        notificationSnapshot.getRef().child("isRead").setValueAsync(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors here
                System.err.println("Error marking all notifications as read: " + databaseError.getMessage());
            }
        });
    }

    public List<Long> getNotificationsAndMatchWithEmployees(Long announcementId, int day) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Long> userIdList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Include milliseconds

        try {
            // Query to find the documents to process
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .orderBy("noticeAt", Query.Direction.DESCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Long userId = document.getLong("userId");
                LOGGER.info("User ID from Firebase service: " + userId);

                LocalDateTime noticeAt = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("noticeAt")), formatter);
                LocalDateTime timestamp = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("timestamp")), formatter);
                LOGGER.info("Parsed noticeAt: " + noticeAt.toString());
                LOGGER.info("Parsed timestamp: " + timestamp.toString());
                String status;
                if (noticeAt.isAfter(timestamp)) {
                    LocalDateTime deadline = noticeAt.plusDays(day);
                    if (LocalDateTime.now().isAfter(deadline)) {
                        status = "Late";
                    } else {
                        status = "Noted";
                        LOGGER.info("Status: " + status);
                        userIdList.add(userId);
                        notedAtStorage.put(userId, noticeAt);
                    }
                } else {
                    status = "Not Noted";
                }
                LOGGER.info("Final Status for User ID " + userId + ": " + status);
            }

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error retrieving or matching data: " + e.getLocalizedMessage());
        }
        return userIdList;
    }

    public void getSelectedAllAnnouncements() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Long> announcementIds = ANNOUNCEMENT_SERVICE.getSelectedAllAnnouncements();
        int announcementCount = ANNOUNCEMENT_SERVICE.getCountSelectAllAnnouncements();
        for (Long announcementId : announcementIds) {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .orderBy("noticeAt", Query.Direction.DESCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Long userId = document.getLong("userId");
                LOGGER.info("User ID from Firebase service: " + userId);

                LocalDateTime noticeAt = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("noticeAt")), formatter);
                LocalDateTime timestamp = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("timestamp")), formatter);
                LOGGER.info("Parsed noticeAt: " + noticeAt.toString());
                LOGGER.info("Parsed timestamp: " + timestamp.toString());
                if (noticeAt.isAfter(timestamp)) {
                    CompletableFuture<Employee> comFuEmployee = EMPLOYEE_SERVICE.findById(userId)
                            .thenApply(employee -> employee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
                    Long companyId = comFuEmployee.join().getCompany().getId();
                    if (employeeCountMap.containsKey(companyId)) {
                        int currentCount = employeeCountMap.getOrDefault(companyId, 0);
                        employeeCountMap.put(companyId, currentCount + 1 );
                    }
                }
            }
        }

    }

    public void updateNoticeAtInFirebase(Long employeeId, long announcementId, String formattedNow) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        try {
            // Query to find the document to update, with sorting
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("userId", employeeId)
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .orderBy("userId", Query.Direction.ASCENDING)
                    .orderBy("announcementId", Query.Direction.ASCENDING)
                    .orderBy("targetId", Query.Direction.ASCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .orderBy("noticeAt", Query.Direction.DESCENDING)
                    .orderBy("__name__", Query.Direction.DESCENDING)
                    .limit(1)
                    .get();

            // Get the document reference
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentReference documentReference = documents.get(0).getReference();

                // Update the noticeAt field in the document
                Map<String, Object> updates = new HashMap<>();
                updates.put("noticeAt", formattedNow);

                ApiFuture<WriteResult> writeResult = documentReference.update(updates);
                LOGGER.info("Firebase noticeAt updated successfully for document ID: " + documentReference.getId() +
                        " at time: " + writeResult.get().getUpdateTime());
            } else {
                LOGGER.warning("No matching document found to update noticeAt.");
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error updating noticeAt in Firestore: " + e.getLocalizedMessage());
        }
    }


//    public void insertIntoTableFromFirebase(Long employeeId, long announcementId) {
//        Firestore dbFirestore = FirestoreClient.getFirestore();
//
//        try {
//            // Query to find the document to insert, with sorting
//            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
//                    .whereEqualTo("userId", employeeId)
//                    .whereEqualTo("announcementId", String.valueOf(announcementId))
//                    .orderBy("receiverType", Query.Direction.DESCENDING)
//                    .orderBy("receiverId", Query.Direction.ASCENDING)
//                    .orderBy("announcementId", Query.Direction.ASCENDING)
//                    .orderBy("noticeAt", Query.Direction.DESCENDING)
//                    .orderBy("timestamp", Query.Direction.DESCENDING)
//                    .orderBy("__name__", Query.Direction.DESCENDING)
//                    .limit(1)
//                    .get();
//
//            // Get the document reference
//            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//            if (!documents.isEmpty()) {
//                DocumentSnapshot document = documents.get(0);
//
//                // Prepare data for insertion
//                Map<String, Object> insertData = new HashMap<>();
//                insertData.put("employeeId", document.getLong("userId"));
//                insertData.put("announcementId", document.getString("announcementId"));
//                insertData.put("receiverType", document.getString("receiverType"));
//                insertData.put("receiverId", document.getString("receiverId"));
//                insertData.put("noticeAt", document.getString("noticeAt"));
//                insertData.put("timestamp", document.getString("timestamp"));
//                // Add more fields as needed
//
//                // Example insertion into a table
//                // Assuming you have a method `insertIntoDatabase(Map<String, Object> data)` in your service to handle the database insertion
//                insertIntoDatabase(insertData);
//
//                LOGGER.info("Firebase data inserted successfully for document ID: " + document.getId());
//            } else {
//                LOGGER.warning("No matching document found to insert.");
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            LOGGER.severe("Error inserting data from Firestore: " + e.getLocalizedMessage());
//        }
//    }
//
//    // Example method to insert data into your database
//    private void insertIntoDatabase(Map<String, Object> data) {
//        // Implement your database insertion logic here
//        // For example, using JDBC or JPA to insert data into your database
//    }

}