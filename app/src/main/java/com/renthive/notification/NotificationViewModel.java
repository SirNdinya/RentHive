package com.renthive.notification;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class NotificationViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public NotificationViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for sending and displaying notifications
}