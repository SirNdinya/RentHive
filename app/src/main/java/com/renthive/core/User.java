package com.renthive.core;

import androidx.annotation.NonNull;

public abstract class User {
    protected String userID;  // Unique Firebase user ID
    protected String name;    // User's full name
    protected String email;   // Email for authentication
    protected String username;
    protected String password; // Password (used for authentication only)
    protected Role role;    // User role: TENANT, PROPERTY_OWNER, or ADMIN

    // Enum for user roles
    public enum Role {
        PROPERTY_OWNER, TENANT, ADMIN
    }

    // Default constructor (required for Firestore serialization)
    public User() {}

    // Constructor to initialize a new user
    public User(String name, String username, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;  // Store as enum
    }

    // Getters and setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setRole(String role) {
        try {
            this.role = Role.valueOf(role.toUpperCase()); // Convert string to enum safely
        } catch (IllegalArgumentException e) {
            this.role = null; // Handle unknown role case
        }
    }


    public String getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
    }




    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", username= '" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
