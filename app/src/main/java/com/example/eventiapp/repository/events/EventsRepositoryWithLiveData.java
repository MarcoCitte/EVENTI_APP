package com.example.eventiapp.repository.events;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.source.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.EventsCallback;
import com.example.eventiapp.util.Constants;

import java.util.List;

public class EventsRepositoryWithLiveData implements IEventsRepositoryWithLiveData, EventsCallback {

    private static final String TAG = EventsRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allEventsMutableLiveData;
    private final MutableLiveData<Result> favoriteEventsMutableLiveData;
    private final BaseEventsRemoteDataSource eventsRemoteDataSource;

    public EventsRepositoryWithLiveData(BaseEventsRemoteDataSource eventsRemoteDataSource) {
        allEventsMutableLiveData = new MutableLiveData<>();
        favoriteEventsMutableLiveData = new MutableLiveData<>();
        this.eventsRemoteDataSource = eventsRemoteDataSource;
        this.eventsRemoteDataSource.setEventsCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchEvents(String country, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT) {
            eventsRemoteDataSource.getEvents(country);
        } else {
            //PRENDE IN LOCALE I DATI
        }
        return allEventsMutableLiveData;
    }

    @Override
    public void fetchEvents(String country) {
        eventsRemoteDataSource.getEvents(country);
    }

    @Override
    public MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading) {
        if (isFirstLoading) {
            //PRENDE I BACKUP
        } else {
            //PRENDE DATI LOCALI
        }
        return favoriteEventsMutableLiveData;
    }

    @Override
    public void updateEvents(Events events) {

    }

    @Override
    public void deleteFavoriteEvents() {

    }

    @Override
    public void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate) {
        Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(eventsApiResponse);
        allEventsMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(EventsApiResponse eventsApiResponse) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(Events events, List<Events> favoriteEvents) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Events> events) {

    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Events> favoriteEvents) {

    }

    @Override
    public void onSuccessFromCloudReading(List<Events> eventsList) {

    }

    @Override
    public void onSuccessFromCloudWriting(Events events) {

    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessSynchronization() {

    }

    @Override
    public void onSuccessDeletion() {

    }
}
