package com.example.eventiapp.repository.events;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;

import java.util.List;

public interface IEventsRepositoryWithLiveData {

    MutableLiveData<Result> fetchEvents(String country, String location, String date, String sort, int limit, long lastUpdate);

    void fetchEvents(String country, String location, String date, String sort, int limit);

    MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading);

    MutableLiveData<Result> getCategoryEvents(String category);

    MutableLiveData<Result> getPlaceEvents(String id);

    MutableLiveData<Result> getSingleEvent(long id);

    MutableLiveData<List<String>> getEventsDates(String name);

    void updateEvents(Events events);

    void deleteEvents();

    void deleteFavoriteEvents();
}
