package com.renthive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.renthive.core.FirebaseHelper;
import com.renthive.core.ValidationUtils;

import androidx.annotation.BinderThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.renthive.authentication.AuthViewModel;
import com.renthive.core.PropertyOwner;
import com.renthive.core.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton, registerButton;
    private AuthViewModel authViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Find UI elements
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Observe authentication state
        authViewModel.getIsLoggedInLiveData().observe(this, user -> {
            if (user != null) {
                handleUserLogin(user);
            }
        });

        // Observe authentication errors
        authViewModel.getAuthErrorLiveData().observe(this, error -> {
            if (error != null) {
                ValidationUtils.showErrorDialog(this,"Login Failed");
                //Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up button listeners
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }

    private void loginUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            ValidationUtils.showErrorDialog(this, "All fields are required");
            //Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingIndicator();

        authViewModel.loginUser(username, password);  // Just call the ViewModel method
    }


    private void handleUserLogin(Object user) {
        hideLoadingIndicator();

        // Extract the role of the user by casting the generic Object to a User type
        String role = ((User) user).getRole().toString();

        // Extract the user ID
        String userId = ((User) user).getUserID();


        // Check if the user is a TENANT
        if ("TENANT".equalsIgnoreCase(role)) {
            // Create an intent to navigate to the TenantDashboardActivity
            Intent intent = new Intent(LoginActivity.this, TenantDashboardActivity.class);

            // Attach the tenant's user ID to the intent to pass it to the next activity
            intent.putExtra("tenantId", userId);

            // Start the TenantDashboardActivity
            startActivity(intent);

            // Finish the current LoginActivity to prevent users from navigating back
            finish();
        }
        // Check if the user is a PROPERTY OWNER
        else if ("PROPERTY_OWNER".equalsIgnoreCase(role)) {
            if (user instanceof PropertyOwner) {
                PropertyOwner propertyOwner = (PropertyOwner) user;
                String verificationStatus = propertyOwner.getVerificationStatus();

                if ("Approved".equalsIgnoreCase(verificationStatus)) {
                    // Create an intent to navigate to the PropertyOwnerDashboardActivity
                    Intent intent = new Intent(LoginActivity.this, PropertyOwnerDashboardActivity.class);
                    intent.putExtra("propertyOwnerID", userId);
                    startActivity(intent);
                    finish();
                } else if ("Pending".equalsIgnoreCase(verificationStatus)) {
                    // Show a message if the account is still pending verification
                    ValidationUtils.showErrorDialog(this, "Your account is still pending verification. Please wait for admin approval.");
                } else if ("Rejected".equalsIgnoreCase(verificationStatus)) {
                    // Show a message if the account has been rejected
                    ValidationUtils.showErrorDialog(this, "Your account verification was rejected. Contact support for assistance.");
                }
            }
        }

        // Check if the user is an ADMIN
        else if ("ADMIN".equalsIgnoreCase(role)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("admins").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // Create an intent to navigate to the AdminDashboardActivity
                            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);

                            // Attach the admin's user ID to the intent
                            intent.putExtra("adminId", userId);

                            // Start the AdminDashboardActivity
                            startActivity(intent);

                            // Finish the current LoginActivity
                            finish();
                        } else {
                            // Show an error message if the admin is not found in Firestore
                            ValidationUtils.showErrorDialog(this, "Admin access denied. You are not registered.");
                        }
                    });
        }
        // Handle unknown roles (cases where the role is neither "TENANT", "PROPERTY_OWNER", nor "ADMIN")
        else {
            ValidationUtils.showErrorDialog(this, "Unknown user role.");
        }
    }


    private void showLoadingIndicator() {
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
    }

}
