package com.renthive;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.renthive.authentication.AuthViewModel;
import com.renthive.authentication.UserRepository.OnRegistrationListener;
import com.renthive.core.ValidationUtils;

public class RegistrationActivity extends AppCompatActivity {

    private RadioGroup roleRadioGroup;
    private LinearLayout tenantFields, propertyOwnerFields;
    private AuthViewModel authViewModel;
    private EditText passwordField, confirmPasswordField;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Find views by their IDs in the XML layout
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        tenantFields = findViewById(R.id.tenantFields);
        propertyOwnerFields = findViewById(R.id.propertyOwnerFields);
        Button registerButton = findViewById(R.id.registerButton);

        // Password fields and toggle buttons
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);

        // Set up role selection listener
        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tenantRadioButton) {
                tenantFields.setVisibility(View.VISIBLE);
                propertyOwnerFields.setVisibility(View.GONE);
                Toast.makeText(this, "Creating a Tenant Account", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.propertyOwnerRadioButton) {
                propertyOwnerFields.setVisibility(View.VISIBLE);
                tenantFields.setVisibility(View.GONE);
                Toast.makeText(this, "Creating a Property Owner Account", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up registration button listener
        registerButton.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() {
        // Collect common user input fields
        String name = ((EditText) findViewById(R.id.nameField)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.emailField)).getText().toString().trim();
        String username = ((EditText) findViewById(R.id.usernameField)).getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate required fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty() || confirmPassword.isEmpty()) {
            ValidationUtils.showErrorDialog(this, "All fields are required");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            ValidationUtils.showErrorDialog(this, "Please use a valid email address");
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            ValidationUtils.showErrorDialog(this, "Create a strong password");
            return;
        }
        // Ensure passwords match
        if (!password.equals(confirmPassword)) {
            ValidationUtils.showErrorDialog(this, "Passwords do not match");
            return;
        }

        // Determine the role and additional fields
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = (selectedRoleId == R.id.tenantRadioButton) ? "tenant" : "propertyOwner";
        String additionalField1, additionalField2 = null;

        if (role.equals("tenant")) {
            additionalField1 = ((EditText) findViewById(R.id.universityField)).getText().toString().trim();
            if (additionalField1.isEmpty()){
                ValidationUtils.showErrorDialog(this,"Fill in University");
            }
        } else {
            additionalField1 = ((EditText) findViewById(R.id.phoneNumberField)).getText().toString().trim();
            if (!ValidationUtils.isValidPhoneNumber(additionalField1)) {
                ValidationUtils.showErrorDialog(this, "Invalid Phone Number");
            }
            if (additionalField1.isEmpty()){
                ValidationUtils.showErrorDialog(this, "Phone number cannot be empty");
            }
            additionalField2 = ((EditText) findViewById(R.id.universityAreaField)).getText().toString().trim();
            if (additionalField2.isEmpty()){
                ValidationUtils.showErrorDialog(this, "You must enter the University close to you");
            }
        }

        // Show loading indicator
        showLoadingIndicator();

        // Register the user using AuthViewModel
        authViewModel.registerUser(name, email, password, username, role, additionalField1, additionalField2, new OnRegistrationListener() {
            @Override
            public void onRegistrationSuccess(String role, String userID) {
                // Hide loading indicator
                hideLoadingIndicator();

                if (role.equals("tenant")) {
                    Toast.makeText(RegistrationActivity.this, "Tenant account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Prevent going back to registration
                } else if (role.equals("propertyOwner")) {
                    Toast.makeText(RegistrationActivity.this, "Registration successful! Proceed to upload required documents", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegistrationActivity.this, DocumentUploadActivity.class);
                    intent.putExtra("propertyOwnerID", userID);
                    startActivity(intent);
                    finish(); // Prevent going back to registration
                }
            }

            @Override
            public void onRegistrationFailed(String errorMessage) {
                hideLoadingIndicator();
                ValidationUtils.showErrorDialog(RegistrationActivity.this, errorMessage);
            }

        });
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
