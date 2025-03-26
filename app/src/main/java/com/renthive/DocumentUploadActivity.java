package com.renthive;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class DocumentUploadActivity extends AppCompatActivity {

    private String propertyOwnerID;
    private Button uploadProofButton, uploadIdButton, submitButton;
    private Uri proofOfOwnershipUri, idDocumentUri;
    private FirebaseStorage storage;
    private int filePickerType;
    ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri fileUri = result.getData().getData();
                            if (filePickerType == 1) {
                                proofOfOwnershipUri = fileUri;
                            } else if (filePickerType == 2) {
                                idDocumentUri = fileUri;
                            }
                        }
                    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // Hide initially

        // Get the Property Owner ID from the intent
        propertyOwnerID = getIntent().getStringExtra("propertyOwnerID");

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Find UI elements
        uploadProofButton = findViewById(R.id.uploadProofButton);
        uploadIdButton = findViewById(R.id.uploadIdButton);
        submitButton = findViewById(R.id.submitButton);

        // Set up event listeners
        uploadProofButton.setOnClickListener(v -> openFilePicker(1));
        uploadIdButton.setOnClickListener(v -> openFilePicker(2));
        submitButton.setOnClickListener(v -> uploadDocuments());
    }

    private void openFilePicker(int fileType) {
        filePickerType = fileType;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",  // PDF files
                "image/*",          // Images (JPG, PNG, etc.)
                "application/msword",  // Word (.doc)
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // Word (.docx)
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(intent);
    }

    private void uploadDocuments() {
        if (proofOfOwnershipUri != null && idDocumentUri != null) {
            progressBar.setVisibility(View.VISIBLE); // Show ProgressBar

            StorageReference proofRef = storage.getReference().child("documents/" + propertyOwnerID + "/proof.pdf");
            StorageReference idRef = storage.getReference().child("documents/" + propertyOwnerID + "/id.pdf");

            // Upload proof of ownership
            proofRef.putFile(proofOfOwnershipUri)
                    .addOnSuccessListener(taskSnapshot -> proofRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the download URL to Firestore
                        updateFirestore("proofOfOwnershipUrl", uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            // Upload ID document
            idRef.putFile(idDocumentUri)
                    .addOnSuccessListener(taskSnapshot -> idRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the download URL to Firestore
                        updateFirestore("idDocumentUrl", uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar after both uploads
                        Toast.makeText(this, "Documents uploaded successfully! Your account is under review.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(DocumentUploadActivity.this, LoginActivity.class);
                        intent.putExtra("propertyOwnerID", propertyOwnerID);
                        startActivity(intent);
                        finish();
                    });

        } else {
            Toast.makeText(this, "Please upload both documents.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateFirestore(String field, String url) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put(field, url);

        firestore.collection("propertyOwners")
                .document(propertyOwnerID)
                .set(data, SetOptions.merge()) // Ensures that existing data is not overwritten
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show());
    }
}
