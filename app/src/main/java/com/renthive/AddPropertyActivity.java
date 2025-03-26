package com.renthive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.renthive.R;
import com.renthive.propertymanagement.PropertyViewModel;
import com.renthive.core.Property;
import com.renthive.core.ValidationUtils;

public class AddPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etPropertyName, etPropertyLocation, etPropertyPrice, etPropertyDescription;
    private Button btnSaveProperty, btnSelectImage;
    private ImageView imgProperty;
    private Uri imageUri;
    private PropertyViewModel propertyViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        etPropertyName = findViewById(R.id.etPropertyName);
        etPropertyLocation = findViewById(R.id.etPropertyLocation);
        etPropertyDescription = findViewById(R.id.etPropertyDescription);
        btnSaveProperty = findViewById(R.id.btnSaveProperty);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgProperty = findViewById(R.id.imgProperty);

        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);

        // Observe error messages
        propertyViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnSaveProperty.setOnClickListener(v -> saveProperty());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgProperty.setImageURI(imageUri);
        }
    }

    private void saveProperty() {
        String name = etPropertyName.getText().toString().trim();
        String location = etPropertyLocation.getText().toString().trim();
        String description = etPropertyDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location)  || TextUtils.isEmpty(description) || imageUri == null) {
            ValidationUtils.showError(this, "All fields and image are required!");
            return;
        }

        Property newProperty = new Property(name, location, description, imageUri.toString());

        propertyViewModel.addProperty(newProperty);

        // Observe ViewModel updates for success feedback
        propertyViewModel.getProperties().observe(this, properties -> {
            if (properties != null) {
                Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });
    }

}
