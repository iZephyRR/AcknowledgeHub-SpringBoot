package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
public class NotificationController {
    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CheckingBean CHECKING_BEAN;

    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationDTO notificationDTO) {
        simpMessagingTemplate.convertAndSend("/topic/notifications", notificationDTO);
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

        // Existing fields
        docData.put("title", notificationDTO.getTitle());
        docData.put("category", notificationDTO.getCategoryName());
        docData.put("Sender", CHECKING_BEAN.getRole().toString());
        docData.put("SenderName", CHECKING_BEAN.getName());
        docData.put("userId", notificationDTO.getUserId());
        docData.put("announcementId", String.valueOf(notificationDTO.getAnnouncementId()));
        docData.put("status", notificationDTO.getStatus().toString());
        docData.put("type", notificationDTO.getType().toString());
        docData.put("noticeAt", notificationDTO.getNoticeAt().toString());
        docData.put("timestamp", java.sql.Timestamp.valueOf(notificationDTO.getTimestamp())); // Ensure correct Timestamp type
        docData.put("companyId", notificationDTO.getCompanyId());

        // Adding the targetId field
        if (notificationDTO.getTargetId() != null) {
            docData.put("targetId", notificationDTO.getTargetId().toString());
        }

        return docData;
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


