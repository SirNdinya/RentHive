package com.renthive.propertymanagement;

import androidx.lifecycle.ViewModel;
import com.renthive.core.FirebaseHelper;

public class PropertyViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public PropertyViewModel() {
        firebaseHelper = FirebaseHelper.getInstance();
    }

    // Methods for CRUD operations
}