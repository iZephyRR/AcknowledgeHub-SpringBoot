package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FirebaseNotificationService {

    private static final Logger LOGGER = Logger.getLogger(FirebaseNotificationService.class.getName());
    public final Map<Long, LocalDateTime> notedAtStorage = new HashMap<>();
    private final FirebaseDatabase FIREBASE_DATABASE;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final CheckingBean CHECKING_BEAN;


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

//    public int employeeCountByCompany(Long companyId) {
//        return EMPLOYEE_REPOSITORY.getEmployeeCountByCompanyId(companyId);
//    }

    public void updateNoticeAtInFirebase(Long employeeId, long announcementId, String formattedNow) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        try {
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

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentReference documentReference = documents.get(0).getReference();

                Map<String, Object> updates = new HashMap<>();
                updates.put("noticeAt", formattedNow);

                ApiFuture<WriteResult> writeResult = documentReference.update(updates);
                LOGGER.info("Firebase noticeAt updated successfully for document ID: " + documentReference.getId() +
                        " at time: " + writeResult.get().getUpdateTime());


                DocumentSnapshot docSnapshot = documents.get(0);
                String targetName = docSnapshot.getString("targetName");
                String title = docSnapshot.getString("title");
                String senderName = docSnapshot.getString("SenderName");
                String senderId = docSnapshot.getString("SenderId");
                String message = targetName + " has noted the announcement: " + title;
                saveToNotedCollection(announcementId, targetName, title, senderName, senderId, formattedNow, dbFirestore);


                sendNotificationsToHR(announcementId, targetName, title, senderName, senderId, dbFirestore);} else {
                LOGGER.warning("No matching document found to update noticeAt.");
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error updating noticeAt in Firestore: " + e.getLocalizedMessage());
        }
    }

    private void saveToNotedCollection(Long announcementId, String targetName, String title, String senderName, String senderId, String formattedNow, Firestore dbFirestore) {
        Map<String, Object> notedData = new HashMap<>();
        notedData.put("announcementId", announcementId);
        notedData.put("targetName", targetName);
        notedData.put("title", title);
        notedData.put("noticeAt", formattedNow);
        notedData.put("SenderName", senderName);
        notedData.put("SenderId", senderId);

        LOGGER.info("Noted data saved for announcement ID: " + announcementId);
    }

    private void sendNotificationsToHR(Long announcementId, String targetName, String title, String senderName, String senderId, Firestore dbFirestore) throws InterruptedException, ExecutionException {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatDate);
        List<Employee> hrEmployees = EMPLOYEE_REPOSITORY.findEmployeesByRolesAndAnnouncement(
                Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE),
                announcementId);

        for (Employee hrEmployee : hrEmployees) {
            CHECKING_BEAN.setId(hrEmployee.getId());
            CHECKING_BEAN.setCompanyId(hrEmployee.getCompany().getId());
            CHECKING_BEAN.setRole(hrEmployee.getRole());
            CHECKING_BEAN.setStatus(hrEmployee.getStatus());

            if (isValidForNotification(CHECKING_BEAN)) {

                Map<String, Object> hrNotification = new HashMap<>();
                hrNotification.put("announcementId", announcementId);
                hrNotification.put("targetId", hrEmployee.getId());
                hrNotification.put("message", targetName + " has noted the announcement: " + title);
                hrNotification.put("noticeAt", formattedDateTime);
                hrNotification.put("SenderName", senderName);
                hrNotification.put("SenderId", senderId);


                dbFirestore.collection("notifications").add(hrNotification).get();

                Map<String, Object> notedData = new HashMap<>();
                notedData.put("announcementId", announcementId);
                notedData.put("targetId", hrEmployee.getId());
                notedData.put("targetName", targetName);
                notedData.put("message", targetName + " has noted the announcement: " + title);
                notedData.put("title", title);
                notedData.put("noticeAt", formattedDateTime);
                notedData.put("SenderName", senderName);
                notedData.put("SenderId", senderId);
                notedData.put("timestamp", formattedDateTime);

                dbFirestore.collection("noted").add(notedData).get();

                LOGGER.info("Noted data saved for HR employee: " + hrEmployee.getName());
            }

            CHECKING_BEAN.refresh();
        }
    }

    private boolean isValidForNotification(CheckingBean checkingBean) {

        if (checkingBean.getRole() == EmployeeRole.MAIN_HR ||
                checkingBean.getRole() == EmployeeRole.HR ||
                checkingBean.getRole() == EmployeeRole.HR_ASSISTANCE){
            return true;
        }
        return false;
    }

    public void saveComment(Long commentId,Long announcementId, Long commenterId, String commenterName, String content) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTimes = LocalDateTime.now().format(formatDate);
        Firestore dbFirestore = FirestoreClient.getFirestore();

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("commentId", commentId);
        commentData.put("announcementId", announcementId);
        commentData.put("commenterId", commenterId);
        commentData.put("commenterName", commenterName);
        commentData.put("content", content);
        commentData.put("timestamp", formattedDateTimes);

        ApiFuture<WriteResult> future = dbFirestore.collection("comments").document(String.valueOf(commentId)).set(commentData);
        future.addListener(() -> {
            try {
                future.get();
                LOGGER.info("Comment saved to Firebase for announcement: " + announcementId);
                sendNotificationToCreator(announcementId, commenterName, content);
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.severe("Failed to save comment to Firebase: " + e.getMessage());
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void sendNotificationToCreator(Long announcementId, String commenterName, String content) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTimes = LocalDateTime.now().format(formatDate);
        Firestore dbFirestore = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.get(0);
                String SenderID = document.getString("SenderID");
                String SenderName = document.getString("SenderName");

                if (SenderID != null && SenderName != null) {
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("announcementId", announcementId);
                    notificationData.put("message", commenterName + " commented: " + content);
                    notificationData.put("noticeAt", formattedDateTimes);
                    notificationData.put("timestamp",formattedDateTimes);
                    notificationData.put("commentSenderName", commenterName);
                    notificationData.put("SenderName", SenderName);
                    notificationData.put("sentTo", SenderName);
                    notificationData.put("targetId", SenderID);
                    notificationData.put("type", "NEW_COMMENT");
                    notificationData.put("isRead", false);
                    dbFirestore.collection("notifications_for_comment").add(notificationData).get();
                    LOGGER.info("Notification sent to sender for comment on announcement ID: " + announcementId);
                } else {
                    LOGGER.warning("No SenderID or SenderName found in the notification for announcement ID: " + announcementId);
                }
            } else {
                LOGGER.warning("No notification found for announcement ID: " + announcementId);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error sending notification to creator: " + e.getLocalizedMessage());
        }
    }

    public void saveReply(Long announcementId,String replyId, String replierId, String replierName, String content, String originalCommenterId) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTimes = LocalDateTime.now().format(formatDate);
        Firestore dbFirestore = FirestoreClient.getFirestore();

        Map<String, Object> replyData = new HashMap<>();
        replyData.put("replyId", replyId);
        replyData.put("commentId", originalCommenterId);
        replyData.put("replierId", replierId);
        replyData.put("replierName", replierName);
        replyData.put("message", replierName + " replied to your comment " + content);
        replyData.put("noticeAt",formattedDateTimes);
        replyData.put("timestamp", formattedDateTimes);
        replyData.put("announcementId", announcementId);

        ApiFuture<WriteResult> future = dbFirestore.collection("replies").document(replyId).set(replyData);

        future.addListener(() -> {
            try {
                future.get();
                LOGGER.info("Reply saved to Firebase for comment ID: " + originalCommenterId);
                //sendNotificationToOriginalCommenter(originalCommenterId, replierName, content, Long.valueOf(originalCommenterId));
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.severe("Failed to save reply to Firebase: " + e.getMessage());
            }
        }, Executors.newSingleThreadExecutor());
    }

    private void sendNotificationToOriginalCommenter(String commentId, String replierName, String content, Long originalCommenterId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTimes = LocalDateTime.now().format(formatDate);

        LOGGER.info("Fetching comment with commentId: " + commentId);

        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("comments")
                    .whereEqualTo("commentId", commentId)
                    .limit(1)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.get(0);
                String originalCommenterName = document.getString("commenterName");

                if (originalCommenterName != null) {
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("commenterId", commentId);
                    notificationData.put("message", replierName + " replied: " + content);
                    notificationData.put("noticeAt", formattedDateTimes);
                    notificationData.put("SenderName", replierName);
                    notificationData.put("type", "NEW_REPLY");
                    notificationData.put("receiverName", originalCommenterName);

                    dbFirestore.collection("notifications_for_replies").add(notificationData).get();
                    LOGGER.info("Notification sent to original commenter for reply on comment ID: " + commentId);
                } else {
                    LOGGER.warning("No commenter found for comment ID: " + commentId);
                }
            } else {
                LOGGER.warning("No comment found for comment ID: " + commentId);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error sending notification to original commenter: " + e.getLocalizedMessage());
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