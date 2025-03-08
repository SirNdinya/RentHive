package com.renthive.authentication;

import com.renthive.core.FirebaseHelper;

public class UserRepository {
    private FirebaseHelper firebaseHelper;

    public UserRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firebase Authentication interactions
    // TODO: Implement user registration (Landlord, Tenant, Admin)
    // TODO: Implement user login with email/password
 // TODO: Implement role-based access control
 // TODO: Implement logout functionality
}