package com.renthive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.renthive.core.Property;
import com.renthive.core.ValidationUtils;
import com.renthive.listingmanagement.ListingViewModel;
import com.renthive.AddPropertyActivity;
import com.renthive.adapters.PropertyAdapter;
import com.renthive.propertymanagement.PropertyViewModel;
import java.util.List;

public class PropertyOwnerDashboardActivity extends AppCompatActivity {

    private TextView tvOwnerName, tvOwnerEmail;
    private ImageView imgOwnerProfile;
    private RecyclerView recyclerViewProperties;
    private PropertyAdapter propertyAdapter;
    private PropertyViewModel propertyViewModel;
    private ListingViewModel listingViewModel;
    private Button btnAddProperty, btnManageListings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_owner_dashboard);

        tvOwnerName = findViewById(R.id.tvOwnerName);
        tvOwnerEmail = findViewById(R.id.tvOwnerEmail);
        imgOwnerProfile = findViewById(R.id.imgOwnerProfile);
        recyclerViewProperties = findViewById(R.id.recyclerViewProperties);
        btnAddProperty = findViewById(R.id.btnAddProperty);
        btnManageListings = findViewById(R.id.btnManageListings);

        propertyViewModel = new ViewModelProvider(this).get(PropertyViewModel.class);
        listingViewModel = new ViewModelProvider(this).get(ListingViewModel.class);

        propertyAdapter = new PropertyAdapter();
        recyclerViewProperties.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProperties.setAdapter(propertyAdapter);

        propertyViewModel.getProperties().observe(this, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                propertyAdapter.submitList(properties);
            }
        });

        propertyViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                ValidationUtils.showError(this, errorMessage);
            }
        });

        listingViewModel.getError().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                ValidationUtils.showError(this, errorMessage);
            }
        });

        btnAddProperty.setOnClickListener(v -> {
            Intent intent = new Intent(PropertyOwnerDashboardActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        btnManageListings.setOnClickListener(v -> {
            Intent intent = new Intent(PropertyOwnerDashboardActivity.this, AddListingActivity.class);
            startActivity(intent);
        });
    }
}
