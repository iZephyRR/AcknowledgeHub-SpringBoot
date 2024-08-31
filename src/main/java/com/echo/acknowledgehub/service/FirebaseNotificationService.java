package com.echo.acknowledgehub.service;

import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

@Service
public class FirebaseNotificationService {

    private final FirebaseDatabase firebaseDatabase;

    public FirebaseNotificationService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public void markAsRead(String notificationId, String userId) {
        DatabaseReference ref = firebaseDatabase.getReference("notifications/" + userId + "/" + notificationId);
        ref.child("isRead").setValueAsync(true);
    }

    public void markAllAsRead(String userId) {
        DatabaseReference ref = firebaseDatabase.getReference("notifications/" + userId);

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

}
