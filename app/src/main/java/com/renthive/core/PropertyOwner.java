package com.renthive.core;

import java.util.List;

public class PropertyOwner extends User {
    private String propertyOwnerID;
    private String universityArea;
    private String verificationStatus;  // "Pending", "Approved", or "Rejected"
    private String phoneNumber;

    private List<Property> properties;

    // Constructor
    public PropertyOwner(String username, String name, String email, String password, Role propertyOwner, String phoneNumber, String pending) {
        super(name, email, username, password, Role.PROPERTY_OWNER);
        this.propertyOwnerID = generatePropertyOwnerID();
        this.verificationStatus = "Pending";  // Default status
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getPropertyOwnerID() {
        return propertyOwnerID;
    }

    public void setPropertyOwnerID(String propertyOwnerID) {
        this.propertyOwnerID = propertyOwnerID;
    }

    private String generatePropertyOwnerID() {
        return "OWN-" + System.currentTimeMillis();
    }

    public String getUniversityArea() {
        return universityArea;
    }

    public void setUniversityArea(String universityArea) {
        this.universityArea = universityArea;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void approveOwner() {
        this.verificationStatus = "Approved";
    }

    public void rejectOwner() {
        this.verificationStatus = "Rejected";
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
