package com.renthive.favorites;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class FavoriteViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public FavoriteViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for adding/removing favorites
}