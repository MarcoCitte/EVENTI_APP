package com.example.eventiapp.source.user;

import static com.example.eventiapp.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.example.eventiapp.util.Constants.INVALID_USER_ERROR;
import static com.example.eventiapp.util.Constants.UNEXPECTED_ERROR;
import static com.example.eventiapp.util.Constants.USER_COLLISION_ERROR;
import static com.example.eventiapp.util.Constants.WEAK_PASSWORD_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventiapp.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


/**
 * Class that manages the user authentication using Firebase Authentication.
 */
public class UserAuthenticationRemoteDataSource extends BaseUserAuthenticationRemoteDataSource {

    private static final String TAG = UserAuthenticationRemoteDataSource.class.getSimpleName();

    private final FirebaseAuth firebaseAuth;

    public UserAuthenticationRemoteDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            return new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getUid());
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    firebaseAuth.removeAuthStateListener(this);
                    Log.d(TAG, "User logged out");
                    userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.getInstance().signOut();

    }

    @Override
    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(new User(
                            firebaseUser.getDisplayName(), email, firebaseUser.getUid()));
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    userResponseCallback.onSuccessFromAuthentication(new User(
                            firebaseUser.getDisplayName(), email, firebaseUser.getUid()));
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        userResponseCallback.onSuccessFromAuthentication(new User(
                                firebaseUser.getDisplayName(), firebaseUser.getEmail(),
                                firebaseUser.getUid()));
                    } else {
                        userResponseCallback.onFailureFromAuthentication(
                                getErrorMessage(task.getException()));
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
    }

    @Override
    public void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "sendResetPasswordEmail:success");
                //da corr,
                userResponseCallback.onSuccessFromResetPassword("Reset password email sent.");
            }else {
                Log.w(TAG, "sendResetPasswordEmail:failure", task.getException());
                userResponseCallback.onFailureFromResetPassword(getErrorMessage(task.getException()));
            }

        });
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            userResponseCallback.onSuccessFromChangePassword("Password changed successfully");
                        } else {
                            userResponseCallback.onFailureFromChangePassword("Failed to change password");
                        }
                    });
                } else {
                    userResponseCallback.onFailureFromChangePassword("Invalid old password");
                }
            });
        }
    }

    @Override
    public void getLoggedUserProvider() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Objects.requireNonNull(firebaseUser).getProviderData().size() > 0) {
            String provider = firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId();
            userResponseCallback.onSuccessFromProvider(provider);
        }else{
            userResponseCallback.onFailureProvider("Error getting user provider.");

        }
    }

    private String getErrorMessage(Exception exception) {

        if (exception instanceof FirebaseAuthWeakPasswordException) {
            return WEAK_PASSWORD_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return INVALID_CREDENTIALS_ERROR;
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            return INVALID_USER_ERROR;
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return USER_COLLISION_ERROR;
        }
        return UNEXPECTED_ERROR;
    }
}
