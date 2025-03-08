package com.renthive.notification;

import com.renthive.core.FirebaseHelper;

public class NotificationRepository {
    private FirebaseHelper firebaseHelper;

    public NotificationRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firebase Cloud Messaging interactions// TODO: Send push notifications for new messages, bid updates, etc.
    //// TODO: Display in-app notifications
    //// TODO: Allow users to mark notifications as read


}