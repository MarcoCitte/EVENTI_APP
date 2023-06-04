package com.example.eventiapp.source.user;

import com.example.eventiapp.model.User;
import com.example.eventiapp.repository.user.UserResponseCallback;



/**
 * Base class to manage the user authentication.
 */
public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String email, String password);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);
    public abstract void resetPassword(String email);
    public abstract void changePassword(String oldPassword, String newPassword);

}
