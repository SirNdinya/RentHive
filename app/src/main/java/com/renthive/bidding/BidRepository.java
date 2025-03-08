package com.renthive.bidding;

import com.renthive.core.FirebaseHelper;

public class BidRepository {
    private FirebaseHelper firebaseHelper;

    public BidRepository() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for Firestore interactions// TODO: Allow tenants to place bids on properties
    //// TODO: Allow landlords to view and accept/reject bids
    //// TODO: Notify tenants about bid status updates

}