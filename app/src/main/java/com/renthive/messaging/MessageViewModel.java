/*
package com.renthive.messaging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.renthive.core.Message;
import java.util.List;

*/
/**
 * Mediates between Repository (data) and Activity (UI)
 * Maintains message state and handles business logic
 *//*

public class MessageViewModel extends ViewModel {
    // Reference to data repository
    private final MessageRepository repository;

    // LiveData for observing message changes
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public MessageViewModel() {
        this.repository = new MessageRepository();
    }

    */
/**
     * Sends a message via the repository
     * @param message - The message to send
     * @param listener - Callback for completion status
     *//*

    public void sendMessage(Message message, OnCompleteListener<Void> listener) {
        repository.sendMessage(message, listener);
    }

    */
/**
     * Loads messages for a specific thread
     * @param threadId - The conversation thread ID
     *//*

    public void loadMessages(String threadId) {
        repository.getMessages(threadId, (snapshot, error) -> {
            if (error != null) {
                // Handle error case
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                // Convert Firestore documents to Message objects
                messages.setValue(snapshot.toObjects(Message.class));
            }
        });
    }

    */
/**
     * Provides LiveData for UI observation
     * @return LiveData containing list of messages
     *//*

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    @Override
    public void onCleared() {
        // Clean up when ViewModel is destroyed
        repository.removeListener();
        super.onCleared();
    }
}*/
