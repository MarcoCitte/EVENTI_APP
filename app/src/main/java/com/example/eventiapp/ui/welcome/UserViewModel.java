package com.example.eventiapp.ui.welcome;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Result;
import com.example.eventiapp.model.User;
import com.example.eventiapp.repository.user.IUserRepository;



public class UserViewModel extends ViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();

    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> userFavoriteEventsMutableLiveData;
    //private MutableLiveData<Result> userPreferencesMutableLiveData;

    private MutableLiveData<String> userProvider;
    private boolean authenticationError;
    private boolean passwordResetEmailError;
    private boolean passwordChangeError;


    private MutableLiveData<Result> resetPasswordMutableLiveData;
    private MutableLiveData<Result> changePasswordMutableLiveData;


    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
        passwordResetEmailError = false;
        passwordChangeError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getResetPasswordMutableLiveData(String email) {
        resetPasswordMutableLiveData = userRepository.resetPassword(email);
        return resetPasswordMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserFavoriteEventsMutableLiveData(String idToken) {
        if (userFavoriteEventsMutableLiveData == null) {
            getUserFavoriteEvents(idToken);
        }
        return userFavoriteEventsMutableLiveData;
    }
/*
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }
*/
    public User getLoggedUser() {
        return userRepository.getLoggedUser();
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    private void getUserFavoriteEvents(String idToken) {
        userFavoriteEventsMutableLiveData = userRepository.getUserFavoriteEvents(idToken);
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }

    public void changePassword(String oldPassword, String newPassword) {
        userRepository.changePassword(oldPassword, newPassword);

    }
    public MutableLiveData<Result> getChangePasswordMutableLiveData(String oldPassword, String newPassword) {
        if(changePasswordMutableLiveData == null)
            changePasswordMutableLiveData = userRepository.changePassword(oldPassword, newPassword);
        return changePasswordMutableLiveData;
    }


    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public boolean isPasswordResetEmailError() {
        return passwordResetEmailError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    public void setPasswordResetEmailError(boolean passwordResetEmailError) {
        this.passwordResetEmailError = passwordResetEmailError;
    }

    public boolean isPasswordChangeError() {
        return passwordChangeError;
    }

    public void setPasswordChangeError(boolean passwordChangeError) {
        this.passwordChangeError = passwordChangeError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    private void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }


    public MutableLiveData<String> getLoggedUserProvider() {
        userProvider = userRepository.getLoggedUserProvider();
        return userProvider;
    }


}
