package com.renthive.core;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class AdminHelper {
    private static AdminHelper instance;
    private FirebaseFirestore firestore;

    private AdminHelper() {
        firestore = FirebaseHelper.getInstance().getFirestore();
    }

    public static synchronized AdminHelper getInstance() {
        if (instance == null) {
            instance = new AdminHelper();
        }
        return instance;
    }

    /**
     * Fetch all users (Property Owners & Tenants)
     */
    public void getAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection("users").get().addOnCompleteListener(listener);
    }

    /**
     * Fetch property owners pending verification
     */
    public void getPendingVerifications(OnCompleteListener<QuerySnapshot> listener) {
        firestore.collection("users")
                .whereEqualTo("verificationStatus", "pending")
                .get()
                .addOnCompleteListener(listener);
    }

    /**
     * Approve a Property Owner by updating verification status to "approved"
     */
    public void approvePropertyOwner(String userId, OnCompleteListener<Void> listener) {
        updateUserField(userId, "verificationStatus", "approved", listener);
    }

    /**
     * Reject a Property Owner by updating verification status to "rejected"
     */
    public void rejectPropertyOwner(String userId, OnCompleteListener<Void> listener) {
        updateUserField(userId, "verificationStatus", "rejected", listener);
    }

    /**
     * Fetch user details by ID
     */
    public void getUserDetails(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        firestore.collection("users").document(userId).get().addOnCompleteListener(listener);
    }

    /**
     * Delete a user (Property Owner or Tenant)
     */
    public void deleteUser(String userId, OnCompleteListener<Void> listener) {
        firestore.collection("users").document(userId).delete().addOnCompleteListener(listener);
    }

    /**
     * Disable a user account by setting accountStatus to "disabled"
     */
    public void disableUser(String userId, OnCompleteListener<Void> listener) {
        updateUserField(userId, "accountStatus", "disabled", listener);
    }

    /**
     * Enable a user account by setting accountStatus to "active"
     */
    public void enableUser(String userId, OnCompleteListener<Void> listener) {
        updateUserField(userId, "accountStatus", "active", listener);
    }

    /**
     * Generic method to update a field in the "users" collection
     */
    private void updateUserField(String userId, String field, String value, OnCompleteListener<Void> listener) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);

        userRef.update(updates).addOnCompleteListener(listener);
    }
}
