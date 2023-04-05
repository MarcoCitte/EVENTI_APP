package com.example.eventiapp.repository.events;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;

public interface IEventsRepositoryWithLiveData {

    MutableLiveData<Result> fetchEvents(String country, String location, String date, int limit, long lastUpdate);

    void fetchEvents(String country, String location, String date, int limit);

    MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading);

    void updateEvents(Events events);

    void deleteFavoriteEvents();
}
