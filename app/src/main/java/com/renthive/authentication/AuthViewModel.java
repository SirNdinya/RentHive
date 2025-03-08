package com.renthive.authentication;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class AuthViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public AuthViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for login, registration, and logout
}