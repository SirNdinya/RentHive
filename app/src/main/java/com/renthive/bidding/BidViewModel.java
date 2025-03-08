package com.renthive.bidding;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class BidViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public BidViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for placing and managing bids
}