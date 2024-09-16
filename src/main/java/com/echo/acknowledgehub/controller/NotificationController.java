package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.service.FirebaseNotificationService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {

    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CheckingBean CHECKING_BEAN;
    private final Firestore dbFirestore;

    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationDTO notificationDTO) {
        simpMessagingTemplate.convertAndSend("/topic/notifications", notificationDTO);
    }

    @GetMapping("/noted-count/{announcementId}")
    public String countNoted(@PathVariable Long announcementId) {
        try {
            long count = countNotedByAnnouncementId(announcementId, dbFirestore);
            return "There are " + count + " noted records for announcement ID: " + announcementId;
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while counting noted records.";
        }
    }

    public long countNotedByAnnouncementId(Long announcementId, Firestore dbFirestore) throws InterruptedException, ExecutionException {
        Query query = dbFirestore.collection("noted").whereEqualTo("announcementId", announcementId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        long notedCount = querySnapshot.get().size();
        LOGGER.info("Counted " + notedCount + " noted records for announcement ID: " + announcementId);
        return notedCount;
    }

    public void saveNotificationInFirebase(NotificationDTO notificationDTO) {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        // Build the notification data
        Map<String, Object> docData = buildNotificationData(notificationDTO);

        // Save the notification in Firebase
        ApiFuture<DocumentReference> future = dbFirestore.collection("notifications").add(docData);
        try {
            DocumentReference documentReference = future.get();
            LOGGER.info("Notification saved to Firestore with ID: " + documentReference.getId());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error saving notification to Firestore: " + e.getLocalizedMessage());
        }
    }

    private Map<String, Object> buildNotificationData(NotificationDTO notificationDTO) {
        Map<String, Object> docData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Existing fields
        docData.put("title", notificationDTO.getTitle());
        docData.put("category", notificationDTO.getCategoryName());
        docData.put("Sender", CHECKING_BEAN.getRole().toString());
        docData.put("SenderName", CHECKING_BEAN.getName());
        LOGGER.info("noti user get id : " + notificationDTO.getUserId());
        docData.put("userId", notificationDTO.getUserId());
        docData.put("announcementId", String.valueOf(notificationDTO.getAnnouncementId()));
        //docData.put("status", notificationDTO.getStatus().toString());
        docData.put("type", notificationDTO.getType().toString());
        docData.put("noticeAt", notificationDTO.getNoticeAt().format(formatter));
        docData.put("timestamp", notificationDTO.getTimestamp().format(formatter));
        docData.put("receiverType", notificationDTO.getReceiverType());
        docData.put("receiverId", notificationDTO.getReceiverId());
        // Adding the targetId field
        if (notificationDTO.getTargetId() != null) {
            docData.put("targetId", notificationDTO.getTargetId().toString());
            docData.put("targetName", notificationDTO.getTargetName());
        }
        return docData;
    }

    @GetMapping("/getNotedNotifications/{announcementId}")
    public ResponseEntity<List<Map<String, Object>>> getNotedNotifications(@PathVariable Long announcementId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        List<Map<String, Object>> notedNotifications = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("noted")
                    .whereEqualTo("announcementId", announcementId)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Map<String, Object> notedNotification = document.getData();
                notedNotifications.add(notedNotification);
            }

            LOGGER.info("Fetched " + notedNotifications.size() + " noted notifications for announcement ID: " + announcementId);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error retrieving noted notifications: " + e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }

        return ResponseEntity.ok(notedNotifications);
    }
}

//
//    private void saveNotificationToFirestore(NotificationDTO notificationDTO, Long loggedInId) {
//        Firestore db = FirestoreClient.getFirestore();
//
//        // Handling the "notifications" collection
//        ApiFuture<QuerySnapshot> notificationFuture = db.collection("notifications")
//                .whereEqualTo("targetId", notificationDTO.getTargetId())
//                .whereEqualTo("announcementId", notificationDTO.getAnnouncementId())
//                .get();
//
//        try {
//            if (!notificationFuture.get().isEmpty()) {
//                LOGGER.warning("Notification already exists for targetId: " + notificationDTO.getTargetId());
//                return;
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            LOGGER.severe("Error checking for existing notification: " + e.getLocalizedMessage());
//            return;
//        }
//
//        saveNotificationInFirebase(notificationDTO);  // Save only once if it doesn't exist
//    }
//        private void savenotedAtNotificationToFirestore (NotificationDTO notificationDTO, Long loggedInId){
//            Firestore db2 = FirestoreClient.getFirestore();
//
//
//            ApiFuture<QuerySnapshot> notedFuture = db2.collection("noted")
//                    .whereEqualTo("targetId", notificationDTO.getTargetId())
//                    .whereEqualTo("announcementId", notificationDTO.getAnnouncementId())
//                    .get();
//
//            try {
//                if (!notedFuture.get().isEmpty()) {
//                    LOGGER.warning("Note already exists for targetId: " + notificationDTO.getTargetId());
//                    return;
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                LOGGER.severe("Error checking for existing note: " + e.getLocalizedMessage());
//                return;
//            }
//
//            Map<String, String> notedData = new HashMap<>();
//            notedData.put("loggedInId", String.valueOf(loggedInId));
//            notedData.put("title", notificationDTO.getTitle());
//            notedData.put("Target Employee", notificationDTO.getTargetId().toString());
//            notedData.put("notedAt", notificationDTO.getNotedAt().toString());
//
//            // Save to the "noted" collection
//            ApiFuture<DocumentReference> notedAddFuture = db2.collection("noted").add(notedData);
//            try {
//                DocumentReference notedDocumentReference = notedAddFuture.get();
//                LOGGER.info("Note saved to Fire store with ID: " + notedDocumentReference.getId());
//            } catch (InterruptedException | ExecutionException e) {
//                LOGGER.severe("Error saving note to Fire store: " + e.getLocalizedMessage());
//            }
//        }


