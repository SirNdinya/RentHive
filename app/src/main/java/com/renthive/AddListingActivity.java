package com.renthive;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.renthive.R;
import com.renthive.core.Listing;
import com.renthive.core.Property;
import com.renthive.core.ValidationUtils;
import com.renthive.listingmanagement.ListingViewModel;
import com.renthive.propertymanagement.PropertyViewModel;

import java.io.IOException;
import java.util.List;

public class AddListingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Spinner spinnerProperties;
    private EditText etListingPrice, etListingDetails;
    private Button btnSaveListing, btnSelectImage;
    private ImageView imgListingPreview;
    private Uri selectedImageUri;
    private ListingViewModel listingViewModel;
    private PropertyViewModel propertyViewModel;
    private List<Property> propertyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        spinnerProperties = findViewById(R.id.spinnerProperties);
        etListingPrice = findViewById(R.id.etListingPrice);
        etListingDetails = findViewById(R.id.etListingDetails);
        btnSaveListing = findViewById(R.id.btnSaveListing);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgListingPreview = findViewById(R.id.imgListingPreview);

        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);
        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);

        propertyViewModel.getProperties().observe(this, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                propertyList = properties;
                ArrayAdapter<Property> adapter = new ArrayAdapter<>(AddListingActivity.this, android.R.layout.simple_spinner_dropdown_item, properties);
                spinnerProperties.setAdapter(adapter);
            }
        });

        btnSelectImage.setOnClickListener(v -> openGallery());

        btnSaveListing.setOnClickListener(v -> saveListing());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imgListingPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveListing() {
        if (propertyList == null || propertyList.isEmpty()) {
            ValidationUtils.showError(this, "No properties available. Add a property first.");
            return;
        }

        Property selectedProperty = (Property) spinnerProperties.getSelectedItem();
        String price = etListingPrice.getText().toString().trim();
        String details = etListingDetails.getText().toString().trim();

        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(details) || selectedImageUri == null) {
            ValidationUtils.showError(this, "All fields and image selection are required!");
            return;
        }

        Listing newListing = new Listing(selectedProperty, Double.parseDouble(price), details, selectedImageUri.toString());
        listingViewModel.addListing(newListing);
        Toast.makeText(this, "Listing added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
