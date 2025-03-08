package com.renthive.admin;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class AdminViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public AdminViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for admin actions (verification, moderation, banning)
}