package com.echo.acknowledgehub.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class NotificationController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

//    public void sendNotification(String creator, String announcementTitle) {
//        AnnouncementNotification notification = new AnnouncementNotification(creator, announcementTitle);
//        System.out.println("NotificationController : " + notification.getAnnouncementTitle());
//        simpMessagingTemplate.convertAndSend("/topic/notifications", notification);
//
//        // Save notification to Firebase Firestore
//        saveNotificationToFirestore(creator, announcementTitle);
//    }
//
//    private void saveNotificationToFirestore(String creator, String announcementTitle) {
//        Firestore db = FirestoreClient.getFirestore();
//        Map<String, Object> docData = new HashMap<>();
//        docData.put("creator", creator);
//        docData.put("announcementTitle", announcementTitle);
//        docData.put("timestamp", System.currentTimeMillis());
//
//        ApiFuture<DocumentReference> future = db.collection("notifications").add(docData);
//
//        try {
//            DocumentReference documentReference = future.get();
//            System.out.println("Notification saved to Firestore with ID: " + documentReference.getId());
//        } catch (InterruptedException | ExecutionException e) {
//            System.err.println("Error saving notification to Firestore: " + e.getLocalizedMessage());
//        }
//    }
}
