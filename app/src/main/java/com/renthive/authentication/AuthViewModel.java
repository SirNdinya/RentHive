package com.renthive.authentication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.renthive.authentication.UserRepository.OnRegistrationListener;


public class AuthViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> authErrorLiveData;
    private final MutableLiveData<Boolean> isLoggedInLiveData;

    public AuthViewModel() {
        userRepository = new UserRepository();
        userLiveData = new MutableLiveData<>();
        authErrorLiveData = new MutableLiveData<>();
        isLoggedInLiveData = new MutableLiveData<>(userRepository.isUserLoggedIn());
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getAuthErrorLiveData() {
        return authErrorLiveData;
    }

    public LiveData<Boolean> getIsLoggedInLiveData() {
        return isLoggedInLiveData;
    }

    public void registerUser(String name, String username, String email, String password, String role, String additionalField1, String additionalField2, OnRegistrationListener onRegistrationListener) {
        userRepository.registerUser(name, username, email, password, role, additionalField1, additionalField2, new OnRegistrationListener() {
            @Override
            public void onRegistrationSuccess(String role, String userID) {
                isLoggedInLiveData.setValue(true);
                userLiveData.setValue(userRepository.getCurrentUser());
            }

            @Override
            public void onRegistrationFailed(String errorMessage) {
                authErrorLiveData.setValue(errorMessage);
            }
        });
    }

    public void loginUser(String username, String password) {
        userRepository.loginUser(username, password, task -> {
            if (task.isSuccessful()) {
                userLiveData.setValue(userRepository.getCurrentUser());
                isLoggedInLiveData.setValue(true);
            } else {
                String errorMessage = "Login failed.";
                if (task.getException() != null) {
                    String error = task.getException().getMessage();
                    if (error.contains("password is invalid")) {
                        errorMessage = "Incorrect password.";
                    } else if (error.contains("no user record")) {
                        errorMessage = "Invalid username.";
                    }
                }
                authErrorLiveData.setValue(errorMessage);
            }
        });
    }



    public void logoutUser() {
        userRepository.logoutUser();
        isLoggedInLiveData.setValue(false);
        userLiveData.setValue(null);
    }
}