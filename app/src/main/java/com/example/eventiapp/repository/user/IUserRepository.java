package com.example.eventiapp.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Result;
import com.example.eventiapp.model.User;

import java.util.Set;



public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getUserFavoriteEvents(String idToken);
    //MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    MutableLiveData<Result> resetPassword(String email);
    MutableLiveData<Result> changePassword(String oldPassword, String newPassword);
    void signInWithGoogle(String token);

    MutableLiveData<String> getLoggedUserProvider();
    //void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
}
