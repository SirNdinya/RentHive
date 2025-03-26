package com.renthive.propertymanagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.renthive.core.Property;
import java.util.List;

/**
 * ViewModel for managing property-related data with error handling.
 */
public class PropertyViewModel extends ViewModel {

    private final PropertyRepository repository = PropertyRepository.getInstance(); // Singleton repository
    private final MutableLiveData<List<Property>> propertiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>(); // For handling errors

    public LiveData<List<Property>> getProperties() {
        return propertiesLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Loads all properties with error handling.
     */
    public void loadProperties() {
        repository.getAllProperties(
                propertiesLiveData::setValue,
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Adds a new property with error handling.
     */
    public void addProperty(Property property) {
        repository.addProperty(
                property,
                unused -> loadProperties(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Updates a property with error handling.
     */
    public void updateProperty(Property property) {
        repository.updateProperty(
                property,
                unused -> loadProperties(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Deletes a property with error handling.
     */
    public void deleteProperty(String propertyID) {
        repository.deleteProperty(
                propertyID,
                unused -> loadProperties(),
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

    /**
     * Searches for properties by location with error handling.
     */
    public void searchProperties(String location) {
        repository.searchProperties(
                location,
                propertiesLiveData::setValue,
                e -> errorLiveData.setValue(e.getMessage())
        );
    }

}
