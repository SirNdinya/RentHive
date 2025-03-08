package com.renthive.favorites;

import com.renthive.core.FirebaseHelper;

public class FavoriteRepository {
    private FirebaseHelper firebaseHelper;

    public FavoriteRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firestore interactions// TODO: Allow tenants to bookmark properties
    //// TODO: Display saved properties in the tenant’s profile
    //// TODO: Allow tenants to remove properties from favorites

}