package com.example.eventiapp.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;

import java.util.List;

public class EventsAndPlacesViewModel extends ViewModel {

    private static final String TAG = EventsAndPlacesViewModel.class.getSimpleName();

    private final IRepositoryWithLiveData iRepositoryWithLiveData;
    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> eventsListLiveData; //TUTTI GLI EVENTI
    private MutableLiveData<Result> favoriteEventsListLiveData; //EVENTI FAVORITI
    private MutableLiveData<Result> categoryEventsLiveData; //EVENTI APPARTENENTI AD UNA CATEGORIA SPECIFICA
    private MutableLiveData<Result> placeEventsLiveData; //EVENTI CHE SI TENGONO IN UN LUOGO SPECIFICO
    private MutableLiveData<Result> singleEventLiveData; //EVENTO SINGOLO

    //PLACES
    private MutableLiveData<List<Place>> placesListLiveData; //TUTTI I POSTI
    private MutableLiveData<List<Place>> favoritePlacesListLiveData; //POSTI PREFERITI
    private MutableLiveData<Place> singlePlaceLiveData; //POSTO SINGOLO

    public EventsAndPlacesViewModel(IRepositoryWithLiveData iRepositoryWithLiveData) {
        this.iRepositoryWithLiveData = iRepositoryWithLiveData;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    public MutableLiveData<Result> getEvents(String country, String location, String date, String sort, int limit, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEvents(country, location, date, sort, limit, lastUpdate);
        }
        return eventsListLiveData;
    }

    public MutableLiveData<List<Place>> getPlaces() {
        if (placesListLiveData == null) {
            fetchPlaces();
        }
        return placesListLiveData;
    }

    public MutableLiveData<Result> getFavoriteEventsLiveData(boolean isFirstLoading) {
        if (favoriteEventsListLiveData == null) {
            iRepositoryWithLiveData.getFavoriteEvents(isFirstLoading);
        }
        return favoriteEventsListLiveData;
    }

    public MutableLiveData<List<Place>> getFavoritePlacesLiveData(boolean isFirstLoading) {
        if (favoritePlacesListLiveData == null) {
            iRepositoryWithLiveData.getFavoritePlaces(isFirstLoading);
        }
        return favoritePlacesListLiveData;
    }


    public MutableLiveData<Result> getCategoryEventsLiveData(String category) {
        if (categoryEventsLiveData == null) {
            categoryEventsLiveData = iRepositoryWithLiveData.getCategoryEvents(category);
        }
        return categoryEventsLiveData;
    }

    public MutableLiveData<Result> getPlaceEventsLiveData(String id) {
        if (placeEventsLiveData == null) {
            return iRepositoryWithLiveData.getPlaceEvents(id);
        }
        return placeEventsLiveData;
    }

    public MutableLiveData<Result> getSingleEvent(long id) {
        singleEventLiveData = iRepositoryWithLiveData.getSingleEvent(id);
        return singleEventLiveData;
    }

    public MutableLiveData<Place> getSinglePlace(String id) {
        singlePlaceLiveData = iRepositoryWithLiveData.getSinglePlace(id);
        return singlePlaceLiveData;
    }

    public MutableLiveData<List<String>> getEventsDates(String name) {
        return iRepositoryWithLiveData.getEventsDates(name);
    }

    public MutableLiveData<String[]> getMoviesHours(String name) {
        return iRepositoryWithLiveData.getMoviesHours(name);
    }

    public void deleteEvents() {
        iRepositoryWithLiveData.deleteEvents();
    }

    public int getCount() {
        return iRepositoryWithLiveData.getCount();
    }


    public void fetchEvents(String country, String location, String date, String sort, int limit) {
        iRepositoryWithLiveData.fetchEvents(country, location, date, sort, limit);
    }

    public void fetchEvents(String country, String location, String date, String sort, int limit, long lastUpdate) {
        eventsListLiveData = iRepositoryWithLiveData.fetchEvents(country, location, date, sort, limit, lastUpdate);
    }

    public void fetchPlaces() {
        placesListLiveData = iRepositoryWithLiveData.fetchPlaces();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        this.firstLoading = firstLoading;
    }

    public MutableLiveData<Result> getEventsResponseLiveData() {
        return eventsListLiveData;
    }

    public MutableLiveData<List<Place>> getPlacesResponseLiveData() {
        return placesListLiveData;
    }


}
