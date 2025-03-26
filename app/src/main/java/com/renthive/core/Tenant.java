package com.renthive.core;


import androidx.annotation.NonNull;

public class Tenant extends User {
    protected String tenantID;  // Unique tenant ID
    protected String university; // University the tenant is associated with

    // Constructor to initialize a new tenant
    public Tenant(String username, String name, String email, String password, Role tenant, String university) {
        super(username, name, email, password, Role.TENANT);
        this.university = university;
        this.tenantID = generateTenantID();
    }

    // Getter and Setter for tenantID
    public  String getTenantID() {
       return generateTenantID();
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    // Getter and Setter for university
    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    /**
     * Checks if the tenant can save a property to favorites.
     * This is a pure logic method and does not depend on external systems.
     */
    public boolean canSaveProperty() {
        return this.tenantID != null && !this.tenantID.isEmpty();
    }



    private String generateTenantID() {
        return "TNT-" + System.currentTimeMillis();
    }



    @NonNull
    @Override
    public String toString() {
        return "Tenant{" +
                "tenantID='" + tenantID + '\'' +
                ", university='" + university + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username '"+ username + '\''+
                ", role='" + role + '\'' +
                '}';
    }
}