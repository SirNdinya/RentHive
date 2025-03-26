package com.renthive.core;

import java.util.UUID;

/**
 * Represents a rental listing for a vacant room under a property.
 */
public class Listing {
    private String listingID;
    private Property property; // The property this listing belongs to
    private double price;
    private String description;

    private String status; // Available, Rented, Pending, etc.
    private String imageURL; // Image representing the vacant room

    public Listing() {
        // Default constructor for Firestore
    }

    public Listing(Property property, double price, String description, String imageURL) {
        this.listingID = UUID.randomUUID().toString();
        this.property = property;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.status = "Available"; // Default status
    }


    // Getters and Setters
    public String getListingID() { return listingID; }
    public void setListingID(String listingID) { this.listingID = listingID; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }


}

