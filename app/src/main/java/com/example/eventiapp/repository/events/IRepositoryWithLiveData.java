package com.example.eventiapp.repository.events;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;

import java.util.List;

public interface IRepositoryWithLiveData {

    MutableLiveData<Result> fetchEvents(String country, String location, String date, String sort, int limit, long lastUpdate);

    void fetchEvents(String country, String location, String date, String sort, int limit);

    MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading);

    MutableLiveData<Result> getCategoryEvents(String category);

    MutableLiveData<Result> getPlaceEvents(String id);

    MutableLiveData<Result> getSingleEvent(long id);

    MutableLiveData<List<String>> getEventsDates(String name);

    MutableLiveData<String[]> getMoviesHours(String name);

    MutableLiveData<List<Place>> fetchPlaces();

    MutableLiveData<List<Place>> getFavoritePlaces(boolean isFirstLoading);

    MutableLiveData<Place> getSinglePlace(String id);

    void updateEvents(Events events);

    int getCount();

    void deleteEvents();

    void deleteFavoriteEvents();
}