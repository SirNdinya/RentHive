package com.renthive.core;

public class Amenity {
    private String id;
    private String name;
    private String description;

    // Constructor
    public Amenity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString Method
    @Override
    public String toString() {
        return "Amenity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
