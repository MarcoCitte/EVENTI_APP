package com.example.eventiapp.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Events;
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
    private MutableLiveData<Result> eventsFromSearchLiveData; //EVENTI PRESI DALLA SEARCH
    private MutableLiveData<Result> favoriteEventsListLiveData; //EVENTI FAVORITI
    private MutableLiveData<Result> categoryEventsLiveData; //EVENTI APPARTENENTI AD UNA CATEGORIA SPECIFICA
    private MutableLiveData<Result> categoriesEventsLiveData; //EVENTI APPARTENENTI A PIU CATEGORIE
    private MutableLiveData<Result> eventsInADateLiveData; //EVENTI CHE SI TENGONO IN UNA DATA SPECIFICA
    private MutableLiveData<Result> eventsBetweenDatesLiveData; //EVENTI CHE SI TENGONO TRA DUE DATE
    private MutableLiveData<Result> categoryEventsBetweenDatesLiveData; //EVENTI CHE SI TENGONO TRA DUE DATE CON CATEGORIE
    private MutableLiveData<Result> placeEventsLiveData; //EVENTI CHE SI TENGONO IN UN LUOGO SPECIFICO
    private MutableLiveData<Result> singleEventLiveData; //EVENTO SINGOLO
    private MutableLiveData<List<String>> allCategoriesLiveData; //TUTTE LE CATEGORIE DI EVENTI
    private MutableLiveData<List<String>> categoriesInADateLiveData; //TUTTE LE CATEGORIE DI EVENTI IN UNA DATA SPECIFICA


    //PLACES
    private MutableLiveData<List<Place>> placesListLiveData; //TUTTI I POSTI
    private MutableLiveData<Result> favoritePlacesListLiveData; //POSTI PREFERITI
    private MutableLiveData<Place> singlePlaceLiveData; //POSTO SINGOLO
    private MutableLiveData<List<Place>> placesFromSearchLiveData; //POSTI PRESI DALLA SEARCH

    public EventsAndPlacesViewModel(IRepositoryWithLiveData iRepositoryWithLiveData) {
        this.iRepositoryWithLiveData = iRepositoryWithLiveData;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    public MutableLiveData<Result> getEvents(String country, String location, String date, String categories, String sort, int limit, long lastUpdate) {
        if (eventsListLiveData == null) {
            fetchEvents(country, location, date, categories, sort, limit, lastUpdate);
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
            getFavoriteEvents(isFirstLoading);
        }
        return favoriteEventsListLiveData;
    }

    /**
     * It uses the Repository to get the list of favorite news
     * and to associate it with the LiveData object.
     */
    private void getFavoriteEvents(boolean firstLoading) {
        favoriteEventsListLiveData = iRepositoryWithLiveData.getFavoriteEvents(firstLoading);

    }

    public MutableLiveData<Result> getFavoritePlacesLiveData(boolean isFirstLoading) {
        if (favoritePlacesListLiveData == null) {
            iRepositoryWithLiveData.getFavoritePlaces(isFirstLoading);
        }
        return favoritePlacesListLiveData;
    }

    public MutableLiveData<Result> getEventsFromSearchLiveData(String input) {
        eventsFromSearchLiveData = iRepositoryWithLiveData.getEventsFromSearch(input);
        return eventsFromSearchLiveData;
    }

    /**
     * Updates the event status.
     * @param event The event to be updated.
     */
    public void updateEvents(Events event) {
        iRepositoryWithLiveData.updateEvents(event);
    }

    public void updatePlace(Place place) {
        iRepositoryWithLiveData.updatePlace(place);
    }


    public MutableLiveData<Result> getCategoryEventsLiveData(String category) {
        categoryEventsLiveData = iRepositoryWithLiveData.getCategoryEvents(category);
        return categoryEventsLiveData;
    }

    public MutableLiveData<Result> getEventsInADateLiveData(String date) {
        eventsInADateLiveData = iRepositoryWithLiveData.getEventsInADate(date);
        return eventsInADateLiveData;
    }

    public MutableLiveData<Result> getEventsBetweenDatesLiveData(String firstDate, String endDate) {
        eventsBetweenDatesLiveData = iRepositoryWithLiveData.getEventsBetweenDates(firstDate, endDate);
        return eventsBetweenDatesLiveData;
    }

    public MutableLiveData<Result> getCategoryEventsBetweenDatesLiveData(String firstDate, String endDate, List<String> categories) {
        categoryEventsBetweenDatesLiveData = iRepositoryWithLiveData.getCategoryEventsBetweenDates(firstDate, endDate, categories);
        return categoryEventsBetweenDatesLiveData;
    }

    public MutableLiveData<Result> getCategoriesEventsLiveData(List<String> categories) {
        categoriesEventsLiveData = iRepositoryWithLiveData.getCategoriesEvents(categories);
        return categoriesEventsLiveData;
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

    public MutableLiveData<List<Place>> getPlacesFromSearchLiveData(String input) {
        placesFromSearchLiveData = iRepositoryWithLiveData.getPlacesFromSearch(input);
        return placesFromSearchLiveData;
    }

    public MutableLiveData<Place> getSinglePlaceByName(String name) {
        singlePlaceLiveData = iRepositoryWithLiveData.getSinglePlaceByName(name);
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

    public MutableLiveData<List<String>> getAllCategories() {
        allCategoriesLiveData = iRepositoryWithLiveData.getAllCategories();
        return allCategoriesLiveData;
    }

    public MutableLiveData<List<String>> getCategoriesInADate(String date) {
        categoriesInADateLiveData = iRepositoryWithLiveData.getCategoriesInADate(date);
        return categoriesInADateLiveData;
    }


    public void fetchEvents(String country, String location, String date, String categories, String sort, int limit) {
        iRepositoryWithLiveData.fetchEvents(country, location, date, categories, sort, limit);
    }

    public void fetchEvents(String country, String location, String date, String categories, String sort, int limit, long lastUpdate) {
        eventsListLiveData = iRepositoryWithLiveData.fetchEvents(country, location, date, categories, sort, limit, lastUpdate);
    }

    public void fetchPlaces() {
        placesListLiveData = iRepositoryWithLiveData.fetchPlaces();
    }

    public void deletePlaces() {
        iRepositoryWithLiveData.deletePlaces();
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

    public void removeFromFavorite(Events events) {
        iRepositoryWithLiveData.updateEvents(events);
    }

    public void removeFromFavorite(Place place) {
        iRepositoryWithLiveData.updatePlace(place);
    }
}
