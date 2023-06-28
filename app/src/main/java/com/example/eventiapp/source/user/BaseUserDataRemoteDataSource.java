package com.example.eventiapp.source.user;

import com.example.eventiapp.model.User;
import com.example.eventiapp.repository.user.UserResponseCallback;

/**
 * Base class to get the user data from a remote source.
 */
public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getUserFavoriteEvents(String idToken);
    //public abstract void getUserPreferences(String idToken);
    //public abstract void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken);
}
