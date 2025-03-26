package com.renthive.authentication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.renthive.core.FirebaseHelper;
import com.renthive.core.PropertyOwner;
import com.renthive.core.Tenant;
import com.renthive.core.User;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;

public class UserRepository {


    private FirebaseHelper firebaseHelper;

    public UserRepository() {
        firebaseHelper = FirebaseHelper.getInstance(); // Use the singleton instance of FirebaseHelper
    }

    // Register a new user
    public void registerUser(String name, String username, String email, String password, String role, String additionalField1, String additionalField2, OnRegistrationListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if email already exists in Firestore
        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(emailTask -> {
            if (emailTask.isSuccessful() && !emailTask.getResult().isEmpty()) {
                listener.onRegistrationFailed("You already have an account with this email");
            } else {
                // Check if username already exists
                db.collection("users").whereEqualTo("username", username).get().addOnCompleteListener(usernameTask -> {
                    if (usernameTask.isSuccessful() && !usernameTask.getResult().isEmpty()) {
                        listener.onRegistrationFailed("Username is already taken. Choose a different one.");
                    } else {
                        // Proceed with Firebase Authentication registration
                        firebaseHelper.getFirebaseAuth()
                                .createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser firebaseUser = authTask.getResult().getUser();
                                        if (firebaseUser != null) {
                                            String userID = firebaseUser.getUid();

                                            // Create User object based on role
                                            User user;
                                            if (role.equalsIgnoreCase("tenant")) {
                                                user = new Tenant(username, name, email, password, User.Role.TENANT, additionalField1);
                                            } else if (role.equalsIgnoreCase("propertyOwner")) {
                                                user = new PropertyOwner(username, name, email, password, User.Role.PROPERTY_OWNER, additionalField1, "Pending");
                                                ((PropertyOwner) user).setUniversityArea(additionalField2);
                                            }
                                            else {
                                                listener.onRegistrationFailed("Invalid role selected.");
                                                return;
                                            }

                                            user.setUserID(userID); // Set Firebase UID
                                            saveUserToFirestore(user, listener); // Save user details
                                        }
                                    } else {
                                        listener.onRegistrationFailed(authTask.getException() != null ? authTask.getException().getMessage() : "Registration failed.");
                                    }
                                });
                    }
                });
            }
        });
    }



    private void saveUserToFirestore(User user, OnRegistrationListener listener) {
        FirebaseFirestore firestore = firebaseHelper.getFirestore();
        String userID = user.getUserID(); // Firebase UID

        // Store in "users" collection
        firestore.collection("users").document(userID).set(user)
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to add user to users collection", e));

        // Store in role-specific collections
        if (user instanceof Tenant) {
            String tenantID = ((Tenant) user).getTenantID();
            firestore.collection("tenants").document(tenantID).set(user)
                    .addOnSuccessListener(aVoid -> listener.onRegistrationSuccess("tenant", tenantID))
                    .addOnFailureListener(e -> listener.onRegistrationFailed(e.getMessage()));
        } else if (user instanceof PropertyOwner) {
            String propertyOwnerID = ((PropertyOwner) user).getPropertyOwnerID();
            firestore.collection("propertyOwners").document(propertyOwnerID).set(user)
                    .addOnSuccessListener(aVoid -> listener.onRegistrationSuccess("propertyOwner", propertyOwnerID))
                    .addOnFailureListener(e -> listener.onRegistrationFailed(e.getMessage()));
        }
    }



    // Login a user
    public void loginUser(String username, String password, OnCompleteListener<AuthResult> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Check the "users" collection
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Found user in "users" collection
                        String email = task.getResult().getDocuments().get(0).getString("email");
                        if (email != null) {
                            firebaseHelper.getFirebaseAuth()
                                    .signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(listener);
                        } else {
                            listener.onComplete(Tasks.forException(new Exception("Email not found")));
                        }
                    } else {
                        // Step 2: If not found in "users", check the "admins" collection
                        db.collection("admins").whereEqualTo("username", username).get()
                                .addOnCompleteListener(adminTask -> {
                                    if (adminTask.isSuccessful() && !adminTask.getResult().isEmpty()) {
                                        // Found user in "admins" collection
                                        String email = adminTask.getResult().getDocuments().get(0).getString("email");
                                        if (email != null) {
                                            firebaseHelper.getFirebaseAuth()
                                                    .signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(listener);
                                        } else {
                                            listener.onComplete(Tasks.forException(new Exception("Admin email not found")));
                                        }
                                    } else {
                                        // Neither a user nor an admin was found
                                        listener.onComplete(Tasks.forException(new Exception("Invalid username")));
                                    }
                                });
                    }
                });
    }




    // Logout the current user
    public void logoutUser() {
        firebaseHelper.getFirebaseAuth().signOut();
    }

    // Get the current authenticated user
    public FirebaseUser getCurrentUser() {
        return firebaseHelper.getFirebaseAuth().getCurrentUser();
    }

    // Check if a user is logged in
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public interface OnRegistrationListener {
        void onRegistrationSuccess(String role, String userID);
        void onRegistrationFailed(String errorMessage);
    }

}


