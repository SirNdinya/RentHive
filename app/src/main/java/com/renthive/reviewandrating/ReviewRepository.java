package com.renthive.reviewandrating;

import com.renthive.core.FirebaseHelper;

public class ReviewRepository {
    private FirebaseHelper firebaseHelper;

    public ReviewRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firestore interactions// TODO: Allow tenants to submit reviews and ratings for properties
    //// TODO: Display reviews on property details page
    //// TODO: Calculate and display average ratings

}