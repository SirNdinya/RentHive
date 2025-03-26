package com.renthive;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.renthive.core.ValidationUtils;
import com.renthive.listingmanagement.ListingViewModel;
import com.renthive.propertymanagement.PropertyViewModel;
import com.renthive.adapters.ListingAdapter;

public class TenantDashboardActivity extends AppCompatActivity {

    private ListingViewModel listingViewModel;
    private PropertyViewModel propertyViewModel;
    private ListingAdapter listingAdapter;
    private EditText edtSearchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_dashboard);

        // Initialize Views
        edtSearchLocation = findViewById(R.id.edtSearchLocation);
        Button btnViewProperties = findViewById(R.id.btnViewProperties);
        Button btnViewListings = findViewById(R.id.btnViewListings);
        Button btnSearch = findViewById(R.id.btnSearch); // Search Button
        RecyclerView rvListings = findViewById(R.id.rvListings);

        // Initialize RecyclerView
        listingAdapter = new ListingAdapter(this);
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        rvListings.setAdapter(listingAdapter);

        // Initialize ViewModels
        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        // Observe properties and errors
        propertyViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                ValidationUtils.showError(this, errorMessage);
            }
        });

        // Observe listings and errors
        listingViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                ValidationUtils.showError(this, errorMessage);
            }
        });

        // Button listeners
        btnViewProperties.setOnClickListener(v -> {
            // Implement property listing view (e.g., property recycling)
            loadProperties();
        });

        btnViewListings.setOnClickListener(v -> {
            // Implement listing view (load all listings)
            loadListings();
        });

        // Search Button listener
        btnSearch.setOnClickListener(v -> {
            String location = edtSearchLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                listingViewModel.searchListingsByLocation(location); // Filter by location
            } else {
                listingViewModel.loadListings(); // Load all listings if search is empty
            }
        });

        // Initial load of listings
        loadListings();
    }

    private void loadListings() {
        listingViewModel.loadListings(); // Load all listings
        listingViewModel.getListings().observe(this, listings -> {
            if (listings != null && !listings.isEmpty()) {
                listingAdapter.submitList(listings);
            }
        });
    }

    private void loadProperties() {
        propertyViewModel.loadProperties(); // Load properties
        propertyViewModel.getProperties().observe(this, properties -> {
            // Handle property data (update UI, etc.)
            // This could display the properties or take the user to another activity
        });
    }
}
