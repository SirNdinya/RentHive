package com.renthive.messaging;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class MessageViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public MessageViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for sending and receiving messages
}