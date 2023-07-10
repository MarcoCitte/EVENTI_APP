package com.example.eventiapp.repository.eventsAndPlaces;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;

import java.util.List;

public interface IRepositoryWithLiveData {

    MutableLiveData<Result> fetchEvents(String country, String location, String date, String categories, String sort, int limit, long lastUpdate);

    void fetchEvents(String country, String location, String date, String categories, String sort, int limit);

    void addEvent(Events events);

    void addPlace(Place place);

    MutableLiveData<Result> getUsersCreatedEvents(long lastUpdate);

    MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading);

    MutableLiveData<String> getFavoriteCategory();

    MutableLiveData<Result> getFavoriteCategoryEvents();

    MutableLiveData<Result> getCategoryEvents(String category);

    MutableLiveData<Result> getPlaceEvents(String id);

    MutableLiveData<Result> getSingleEvent(long id);

    MutableLiveData<Result> getEventsInADate(String date);

    MutableLiveData<Result> getEventsFromADate(String date);

    MutableLiveData<List<String>> getAllCategories();

    MutableLiveData<List<String>> getCategoriesInADate(String date);

    MutableLiveData<Result> getEventsFromSearch(String input);

    MutableLiveData<Result> getCategoriesEvents(List<String> categories);

    MutableLiveData<Result> getEventsBetweenDates(String firstDate, String endDate);

    MutableLiveData<Result> getCategoryEventsBetweenDates(String firstDate, String endDate, List<String> categories);

    MutableLiveData<List<String>> getEventsDates(String name);

    MutableLiveData<String[]> getMoviesHours(String name);

    MutableLiveData<List<Place>> fetchPlaces();

    MutableLiveData<Result> getFavoritePlaces(boolean isFirstLoading);


    MutableLiveData<Place> getSinglePlace(String id);

    MutableLiveData<List<Place>> getPlacesFromSearch(String input);

    MutableLiveData<Place> getSinglePlaceByName(String name);

    void updateEvents(Events events);

    int getCount();

    void deleteEvents();

    void deletePlaces();

    void updatePlace(Place place);

    MutableLiveData<Result> getMyEvents(boolean firstLoading);

    void deleteMyEvent(Events events);

    MutableLiveData<List<Place>> getUsersCreatedPlaces(long lastUpdate);

    MutableLiveData<List<Place>> getMyPlaces(boolean isFirstLoading);

    void deleteMyPlace(Place place);

    void editEvent(Events oldEvent, Events newEvent);

    void editPlace(Place oldPlace, Place newPlace);
}
