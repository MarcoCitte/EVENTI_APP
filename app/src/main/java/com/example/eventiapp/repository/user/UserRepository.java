package com.example.eventiapp.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.model.User;
import com.example.eventiapp.source.events.BaseEventsLocalDataSource;
import com.example.eventiapp.source.events.EventsCallback;
import com.example.eventiapp.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.eventiapp.source.user.BaseUserDataRemoteDataSource;

import java.util.List;
import java.util.Set;



/**
 * Repository class to get the user information.
 */
public class UserRepository implements IUserRepository, UserResponseCallback, EventsCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseEventsLocalDataSource eventsLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoriteEventsMutableLiveData;
    //private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseEventsLocalDataSource newsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.eventsLocalDataSource = newsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        //this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteEventsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.eventsLocalDataSource.setEventsCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserFavoriteEvents(String idToken) {
        userDataRemoteDataSource.getUserFavoriteEvents(idToken);
        return userFavoriteEventsMutableLiveData;
    }
/*
    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        userDataRemoteDataSource.getUserPreferences(idToken);
        return userPreferencesMutableLiveData;
    }

 */

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        userRemoteDataSource.resetPassword(email);
        return userMutableLiveData;
    }


    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }
/*
    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        userDataRemoteDataSource.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
    }

 */

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Events> newsList) {
        eventsLocalDataSource.insertEvents(newsList);
    }
/*
    @Override
    public void onSuccessFromGettingUserPreferences() {
        //userPreferencesMutableLiveData.postValue(new Result.UserResponseSuccess(null));
    }

 */

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        eventsLocalDataSource.deleteAll();
    }

    @Override
    public void onSuccessFromResetPassword(String message) {
        Result.ResetPasswordSuccess result = new Result.ResetPasswordSuccess(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromResetPassword(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(EventsApiResponse newsApiResponse) {
        Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(newsApiResponse);
        userFavoriteEventsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessDeletion() {
        Result.UserResponseSuccess result = new Result.UserResponseSuccess(null);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessSynchronization() {
        userFavoriteEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(null));
    }

    @Override
    public void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onEventsCategory(List<Events> events) {

    }

    @Override
    public void onEventsPlace(List<Events> events) {

    }

    @Override
    public void onSingleEvent(Events event) {

    }

    @Override
    public void onEventsDates(List<String> dates) {

    }

    @Override
    public void onMoviesHours(String[] hours) {

    }

    @Override
    public void onEventsFavoriteStatusChanged(Events events, List<Events> favoriteEvents) {

    }

    @Override
    public void onEventsFavoriteStatusChanged(List<Events> events) {

    }

    @Override
    public void onDeleteFavoriteEventsSuccess(List<Events> favoriteEvents) {

    }

    @Override
    public void onCount(int count) {

    }


    @Override
    public void onSuccessFromCloudReading(List<Events> eventsList) {

    }

    @Override
    public void onSuccessFromCloudWriting(Events news) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }
}
