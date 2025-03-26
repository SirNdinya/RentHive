package com.renthive;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.renthive.adapters.PropertyOwnerAdapter;
import com.renthive.core.PropertyOwner;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private PropertyOwnerAdapter adapter;
    private List<PropertyOwner> pendingOwners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        pendingOwners = new ArrayList<>();
        adapter = new PropertyOwnerAdapter(pendingOwners);
        recyclerView.setAdapter(adapter);

        fetchPendingPropertyOwners();
    }

    private void fetchPendingPropertyOwners() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference ownersRef = firestore.collection("propertyOwners");

        ownersRef.whereEqualTo("verificationStatus", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pendingOwners.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        PropertyOwner owner = doc.toObject(PropertyOwner.class);
                        if (owner != null) {
                            pendingOwners.add(owner);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load property owners.", Toast.LENGTH_SHORT).show();
                });
    }
}

