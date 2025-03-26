/*
package com.renthive.messaging;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.renthive.core.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static void sendPushNotification(String receiverId, Message message) {
        // Get receiver's FCM token from Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(receiverId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        if (fcmToken != null && !fcmToken.isEmpty()) {
                            sendFcmNotification(fcmToken, message);
                        }
                    }
                });
    }

    private static void sendFcmNotification(String fcmToken, Message message) {
        try {
            RemoteMessage.Builder builder = new RemoteMessage.Builder(fcmToken + "@fcm.googleapis.com")
                    .setMessageId(UUID.randomUUID().toString())
                    .addData("title", "New Message")
                    .addData("body", message.getMessage())
                    .addData("senderId", message.getSenderID())
                    .addData("messageId", message.getMessageID())
                    .addData("click_action", "OPEN_MESSAGE");

            FirebaseMessaging.getInstance().send(builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error sending FCM notification", e);
        }
    }
}*/
