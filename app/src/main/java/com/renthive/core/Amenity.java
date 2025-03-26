package com.renthive.core;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Amenity {
    private String id;
    private String name;
    private String description;

    // A list to store amenities locally
    private static List<Amenity> amenitiesList = new ArrayList<>();
    // Firestore instance for interacting with the Firestore database
    private static FirebaseFirestore firestore = FirebaseHelper.getInstance().getFirestore();

    // Constructor to initialize Amenity object
    public Amenity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // ========================= GETTERS =========================

    // Getter for the amenity ID
    public String getId() {
        return id;
    }

    // Getter for the amenity name
    public String getName() {
        return name;
    }

    // Getter for the amenity description
    public String getDescription() {
        return description;
    }

    // ========================= STATIC METHODS =========================

    /**
     * Adds a new amenity to Firestore if it doesn't already exist.
     * This method is mainly used by admins to register new amenities.
     */
    public static void addAmenityToFirebase(Amenity amenity) {
        // Reference to the Firestore "amenities" collection
        firestore.collection("amenities")
                .document(amenity.getId()) // Use amenity ID as the document ID
                .set(amenity) // Store the amenity data
                .addOnSuccessListener(aVoid -> System.out.println("Amenity added successfully"))
                .addOnFailureListener(e -> System.out.println("Error adding amenity: " + e.getMessage()));
    }

    /**
     * Fetches all available amenities from Firestore and updates the local list.
     * It clears the existing list before fetching new amenities.
     */
    public static void fetchAmenitiesFromFirebase() {
        firestore.collection("amenities")
                .get() // Fetch all documents in the "amenities" collection
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        amenitiesList.clear(); // Clear the existing list before adding fetched amenities
                        QuerySnapshot querySnapshot = task.getResult();
                        // Iterate through the fetched documents
                        for (DocumentSnapshot document : querySnapshot) {
                            Amenity amenity = document.toObject(Amenity.class); // Convert document to Amenity object
                            if (amenity != null) {
                                amenitiesList.add(amenity); // Add the fetched amenity to the list
                            }
                        }
                        System.out.println("Fetched " + amenitiesList.size() + " amenities from Firestore");
                    } else {
                        System.out.println("Error fetching amenities: " + task.getException());
                    }
                });
    }

    /**
     * Fetches an amenity by name. First, it checks locally in amenitiesList.
     * If not found, it queries Firestore and adds the fetched amenity to the list.
     *
     * @param amenityName The name of the amenity to search for.
     * @return The amenity if found, or null if not found.
     */
    public static Amenity getAmenityByNameOrNull(String amenityName) {
        // First, check locally in amenitiesList
        for (Amenity amenity : amenitiesList) {
            if (amenity.getName().equalsIgnoreCase(amenityName)) {
                return amenity; // Return the amenity if found locally
            }
        }

        // If not found locally, query Firestore
        firestore.collection("amenities")
                .whereEqualTo("name", amenityName) // Query for the amenity by name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Amenity amenity = document.toObject(Amenity.class); // Convert document to Amenity
                        if (amenity != null) {
                            amenitiesList.add(amenity); // Cache the fetched amenity
                            System.out.println("Amenity found and added: " + amenity.getName());
                        }
                    } else {
                        System.out.println("Amenity not found by name: " + amenityName);
                    }
                })
                .addOnFailureListener(e -> System.out.println("Error querying Firestore: " + e.getMessage()));

        return null; // Return null if not found
    }

    // ========================= OVERRIDDEN METHODS =========================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Amenity amenity = (Amenity) obj;
        return id != null && id.equals(amenity.id); // Compare amenities based on their ID
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0; // Generate hashcode based on the amenity ID
    }

    @Override
    public String toString() {
        // Return a string representation of the amenity with its ID, name, and description
        return "Amenity{id='" + id + "', name='" + name + "', description='" + description + "'}";
    }
}
