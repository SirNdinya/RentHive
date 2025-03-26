package com.renthive.listingmanagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.renthive.core.Listing;
import java.util.List;

/**
 * ViewModel for managing listing-related data.
 */
public class ListingViewModel extends ViewModel {

    private final com.renthive.listingmanagement.ListingRepository repository = com.renthive.listingmanagement.ListingRepository.getInstance();
    private final MutableLiveData<List<Listing>> listingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<List<Listing>> getListings() {
        return listingsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Loads all listings.
     */
    public void loadListings() {
        repository.getAllListings(
                listingsLiveData::setValue,
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Adds a new listing.
     */
    public void addListing(Listing listing) {
        repository.addListing(
                listing,
                unused -> loadListings(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Updates a listing.
     */
    public void updateListing(Listing listing) {
        repository.updateListing(
                listing,
                unused -> loadListings(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Deletes a listing.
     */
    public void deleteListing(String listingID) {
        repository.deleteListing(
                listingID,
                unused -> loadListings(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Searches for listings by price.
     */
    public void searchListingsByPrice(int price) {
        repository.searchListingsByPrice(
                price,
                listingsLiveData::setValue,
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    public void searchListingsByLocation(String location) {
        repository.searchListingsByLocation(
                location,
                listingsLiveData::setValue,
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

}
