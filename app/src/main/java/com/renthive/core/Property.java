package com.renthive.core;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a property that can be stored in the system.
 */
@IgnoreExtraProperties
public class Property {
    private String propertyID;
    private String ownerID;
    private String location;

    private List<Amenity> amenities;
    private String description;
    private List<String> imageURLs;
    private String nearestUniversity;
    private String area;

    public Property() {
        // Default constructor for Firestore
    }

    public Property(String propertyID, String ownerID, String location,  List<Amenity> amenities,
                    String description, List<String> imageURLs, String nearestUniversity, String area) {
        this.propertyID = propertyID != null ? propertyID : UUID.randomUUID().toString();
        this.ownerID = ownerID;
        this.location = location;
        this.amenities = amenities != null ? amenities : new ArrayList<>();
        this.description = description;
        this.imageURLs = imageURLs != null ? imageURLs : new ArrayList<>();
        this.nearestUniversity = nearestUniversity;
        this.area = area;

    }

    public Property(String name, String location, String description, String string) {
    }

    // Getters and Setters
    public String getPropertyID() { return propertyID; }
    public void setPropertyID(String propertyID) { this.propertyID = propertyID; }

    public String getOwnerID() { return ownerID; }
    public void setOwnerID(String ownerID) { this.ownerID = ownerID; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }


    public List<Amenity> getAmenities() { return amenities; }
    public void setAmenities(List<Amenity> amenities) { this.amenities = amenities; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImageURLs() { return imageURLs; }
    public void setImageURLs(List<String> imageURLs) { this.imageURLs = imageURLs; }

    public String getNearestUniversity() { return nearestUniversity; }
    public void setNearestUniversity(String nearestUniversity) { this.nearestUniversity = nearestUniversity; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getImageUri() {
        if (imageURLs != null && !imageURLs.isEmpty()) {
            return imageURLs.get(0); // Return the first image URL
        }
        return null; // Return null if no image exists
    }


}
