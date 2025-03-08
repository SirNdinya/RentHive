package com.renthive.review;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class ReviewViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public ReviewViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for submitting and retrieving reviews
}