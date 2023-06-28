package com.example.eventiapp.repository.user;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.User;

import java.util.List;


public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);

    void onFailureFromAuthentication(String message);

    void onSuccessFromRemoteDatabase(User user);

    void onSuccessFromRemoteDatabase(List<Events> newsList);

    //void onSuccessFromGettingUserPreferences();
    void onFailureFromRemoteDatabase(String message);

    void onSuccessLogout();

    void onSuccessFromResetPassword(String message);

    void onFailureFromResetPassword(String message);

    void onSuccessFromChangePassword(String message);

    void onFailureFromChangePassword(String message);


    void onSuccessFromProvider(String provider);

    void onFailureProvider(String s);
}
