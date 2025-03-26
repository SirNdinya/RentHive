package com.renthive.listingmanagement;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.renthive.core.FirebaseHelper;
import com.renthive.core.Listing;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repository for handling listing-related Firestore operations.
 */
public class ListingRepository {

    private static ListingRepository instance;
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getFirestore();
    private final CollectionReference listingCollection = db.collection("listings");

    private ListingRepository() {}

    public static synchronized ListingRepository getInstance() {
        if (instance == null) {
            instance = new ListingRepository();
        }
        return instance;
    }

    public void addListing(Listing listing, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        listingCollection.document(listing.getListingID())
                .set(listing)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void updateListing(Listing listing, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        listingCollection.document(listing.getListingID())
                .set(listing)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteListing(String listingID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        listingCollection.document(listingID)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getAllListings(OnSuccessListener<List<Listing>> onSuccess, OnFailureListener onFailure) {
        listingCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Listing> listings = queryDocumentSnapshots.toObjects(Listing.class);
                    onSuccess.onSuccess(listings);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Searches listings by price and returns the closest matches.
     */
    public void searchListingsByPrice(int targetPrice, OnSuccessListener<List<Listing>> onSuccess, OnFailureListener onFailure) {
        listingCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Listing> allListings = queryDocumentSnapshots.toObjects(Listing.class);

                    // Sort listings based on absolute difference from target price
                    Collections.sort(allListings, new Comparator<Listing>() {
                        @Override
                        public int compare(Listing l1, Listing l2) {
                            return Integer.compare((int) Math.abs(l1.getPrice() - targetPrice), (int) Math.abs(l2.getPrice() - targetPrice));
                        }
                    });

                    onSuccess.onSuccess(allListings);
                })
                .addOnFailureListener(onFailure);
    }

    public void searchListingsByLocation(String location, OnSuccessListener<List<Listing>> onSuccess, OnFailureListener onFailure) {
        listingCollection.whereEqualTo("property.location", location)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Listing> listings = queryDocumentSnapshots.toObjects(Listing.class);
                    onSuccess.onSuccess(listings);
                })
                .addOnFailureListener(onFailure);
    }

}
