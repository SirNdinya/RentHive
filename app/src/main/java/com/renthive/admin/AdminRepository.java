package com.renthive.admin;

import com.renthive.core.FirebaseHelper;

public class AdminRepository {
    private FirebaseHelper firebaseHelper;

    public AdminRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firestore interactions// TODO: Allow admins to verify landlords
    //// TODO: Allow admins to moderate property listings
    //// TODO: Allow admins to ban users

}