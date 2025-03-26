package com.renthive.propertymanagement;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.renthive.core.FirebaseHelper;
import com.renthive.core.Property;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repository for handling property-related Firestore operations with error handling.
 */
public class PropertyRepository {

    private static PropertyRepository instance;
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getFirestore();
    private final CollectionReference propertyCollection = db.collection("properties");

    private PropertyRepository() {}

    public static synchronized PropertyRepository getInstance() {
        if (instance == null) {
            instance = new PropertyRepository();
        }
        return instance;
    }

    public void addProperty(Property property, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        propertyCollection.document(property.getPropertyID())
                .set(property)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateProperty(Property property, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        propertyCollection.document(property.getPropertyID())
                .set(property)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteProperty(String propertyID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        propertyCollection.document(propertyID)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllProperties(OnSuccessListener<List<Property>> onSuccess, OnFailureListener onFailure) {
        propertyCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Property> properties = queryDocumentSnapshots.toObjects(Property.class);
                    onSuccess.onSuccess(properties);
                })
                .addOnFailureListener(onFailure);
    }

    public void searchProperties(String location, OnSuccessListener<List<Property>> onSuccess, OnFailureListener onFailure) {
        propertyCollection.whereEqualTo("location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Property> properties = queryDocumentSnapshots.toObjects(Property.class);
                    onSuccess.onSuccess(properties);
                })
                .addOnFailureListener(onFailure);
    }

}