/*
package com.renthive.messaging;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.renthive.core.FirebaseHelper;
import com.renthive.core.Message;
import java.util.List;

*/
/**
 * Handles all direct communication with Firebase Firestore
 * Manages message sending, retrieval, and real-time updates
 *//*

public class MessageRepository {
    // Firebase helper for database access
    private final FirebaseHelper firebaseHelper;

    // Listener for real-time message updates
    private ListenerRegistration messageListener;

    public MessageRepository() {
        this.firebaseHelper = FirebaseHelper.getInstance();
    }

    */
/**
     * Sends a new message to Firestore
     * @param message - The Message object to send
     * @param listener - Callback for completion status
     *//*

    public void sendMessage(Message message, OnCompleteListener<Void> listener) {
        // Add message to "messages" collection with auto-generated ID
        firebaseHelper.getFirestore().collection("messages")
                .document() // Auto-generate document ID
                .set(message) // Set message data
                .addOnCompleteListener(listener); // Notify when complete
    }

    */
/**
     * Sets up real-time listener for messages in a specific thread
     * @param threadId - The conversation thread ID to listen to
     * @param listener - Callback for message updates
     *//*

    public void getMessages(String threadId, EventListener<QuerySnapshot> listener) {
        // Create query for messages in this thread, ordered by timestamp
        messageListener = firebaseHelper.getFirestore().collection("messages")
                .whereEqualTo("threadId", threadId) // Only messages in this thread
                .orderBy("timestamp", Query.Direction.ASCENDING) // Oldest first
                .addSnapshotListener(listener); // Continuous updates
    }

    */
/**
     * Cleans up the message listener when no longer needed
     * Prevents memory leaks
     *//*

    public void removeListener() {
        if (messageListener != null) {
            messageListener.remove(); // Detach from Firestore updates
            messageListener = null;
        }
    }
}*/
