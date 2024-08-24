package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.NotificationDTO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Controller
public class NotificationController {
    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendNotification(NotificationDTO notificationDTO, Long loggedInId) {
        simpMessagingTemplate.convertAndSend("/topic/notifications", notificationDTO);
        saveNotificationToFirestore(notificationDTO, loggedInId);
    }

    private void saveNotificationToFirestore(NotificationDTO notificationDTO, Long loggedInId) {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("notifications")
                .whereEqualTo("targetId", notificationDTO.getTargetId())
                .whereEqualTo("announcementId", notificationDTO.getAnnouncementId())
                .get();

        try {
            if (!future.get().isEmpty()) {
                LOGGER.warning("Notification already exists for targetId: " + notificationDTO.getTargetId());
                return;
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error checking for existing notification: " + e.getLocalizedMessage());
            return;
        }

        Map<String, Object> docData = new HashMap<>();
        docData.put("title", notificationDTO.getTitle());
        docData.put("category", notificationDTO.getCategoryId().toString());
  //    docData.put("companyId", notificationDTO.getCompanyId());
        docData.put("sentTo", notificationDTO.getEmployeeId().toString());
        docData.put("createdBy", loggedInId.toString());
        docData.put("announcementId", notificationDTO.getAnnouncementId());
        docData.put("status", notificationDTO.getStatus().toString());
        docData.put("type", notificationDTO.getType().toString());
        docData.put("noticeAt", notificationDTO.getNoticeAt().toString());
        ApiFuture<DocumentReference> addFuture = db.collection("notifications").add(docData);
        try {
            DocumentReference documentReference = addFuture.get();
            LOGGER.info("Notification saved to Firestore with ID: " + documentReference.getId());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.severe("Error saving notification to Firestore: " + e.getLocalizedMessage());
        }
    }
}
