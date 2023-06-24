package com.example.eventiapp.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;

import java.util.ArrayList;
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
    private MutableLiveData<Result> myEventsListLiveData; //EVENTI CREATI DALL'UTENTE CORRENTE
    private MutableLiveData<List<Place>> myPlacesListLiveData; //POSTI CREATI DALL'UTENTE CORRENTE

    private MutableLiveData<Result> categoryEventsLiveData; //EVENTI APPARTENENTI AD UNA CATEGORIA SPECIFICA
    private MutableLiveData<Result> categoriesEventsLiveData; //EVENTI APPARTENENTI A PIU CATEGORIE
    private MutableLiveData<Result> eventsInADateLiveData; //EVENTI CHE SI TENGONO IN UNA DATA SPECIFICA
    private MutableLiveData<Result> eventsBetweenDatesLiveData; //EVENTI CHE SI TENGONO TRA DUE DATE
    private MutableLiveData<Result> categoryEventsBetweenDatesLiveData; //EVENTI CHE SI TENGONO TRA DUE DATE CON CATEGORIE
    private MutableLiveData<Result> placeEventsLiveData; //EVENTI CHE SI TENGONO IN UN LUOGO SPECIFICO
    private MutableLiveData<Result> singleEventLiveData; //EVENTO SINGOLO
    private MutableLiveData<List<String>> allCategoriesLiveData; //TUTTE LE CATEGORIE DI EVENTI
    private MutableLiveData<List<String>> categoriesInADateLiveData; //TUTTE LE CATEGORIE DI EVENTI IN UNA DATA SPECIFICA
    private MutableLiveData<String> favoriteCategoryLiveData; //CATEGORIA PREFERITA DALL'UTENTE
    private MutableLiveData<Result> favoriteCategoryEventsLiveData; //EVENTI DELLA CATEGORIA PREFERITI NON ANCORA AGGIUNTI AI PREFERITI

    //PLACES
    private MutableLiveData<List<Place>> placesListLiveData; //TUTTI I POSTI
    private MutableLiveData<Result> favoritePlacesListLiveData; //POSTI PREFERITI
    private MutableLiveData<Place> singlePlaceLiveData; //POSTO SINGOLO
    private MutableLiveData<List<Place>> placesFromSearchLiveData; //POSTI PRESI DALLA SEARCH

    private MutableLiveData<Result> usersCreatedEventsMutableLiveData; //EVENTI CREATI DAGLI UTENTI
    private MutableLiveData<List<Place>> usersCreatedPlacesMutableLiveData; //EVENTI CREATI DAGLI UTENTI


    public EventsAndPlacesViewModel(IRepositoryWithLiveData iRepositoryWithLiveData) {
        this.iRepositoryWithLiveData = iRepositoryWithLiveData;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    public void addEvent(Events events) {
        iRepositoryWithLiveData.addEvent(events);



        Result userCreatedEventsResult = usersCreatedEventsMutableLiveData.getValue();

        if (userCreatedEventsResult != null && userCreatedEventsResult.isSuccess()) {
            List<Events> oldUserCreatedEvents = ((Result.EventsResponseSuccess) userCreatedEventsResult).getData().getEventsList();
            oldUserCreatedEvents.add(events);
            Log.e(TAG, "userCreatedEvents size: " + oldUserCreatedEvents.size());
            usersCreatedEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldUserCreatedEvents)));
        }else{
            List<Events> oldUserCreatedEvents = new ArrayList<>();
            oldUserCreatedEvents.add(events);
            usersCreatedEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldUserCreatedEvents)));

        }

        Result myEventsResult = myEventsListLiveData.getValue();

        if (myEventsResult != null && myEventsResult.isSuccess()) {
            List<Events> oldMyEvents = ((Result.EventsResponseSuccess) myEventsResult).getData().getEventsList();
            oldMyEvents.add(events);
            Log.e(TAG, "MyEventsList size:" + oldMyEvents.size());
            myEventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldMyEvents)));
        }else{
            List<Events> oldMyEvents = new ArrayList<>();
            oldMyEvents.add(events);
            myEventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldMyEvents)));

        }


    }

    public void addPlace(Place place){
        iRepositoryWithLiveData.addPlace(place);


        if (usersCreatedPlacesMutableLiveData != null) {
            List<Place> oldUserCreatedPlaces = usersCreatedPlacesMutableLiveData.getValue();
            oldUserCreatedPlaces.add(place);
            Log.e(TAG, "userCreatedEvents size: " + oldUserCreatedPlaces.size());
            usersCreatedPlacesMutableLiveData.postValue(oldUserCreatedPlaces);
        }else{
            usersCreatedPlacesMutableLiveData = new MutableLiveData<>();
            List<Place> oldUserCreatedPlaces2 = new ArrayList<>();
            oldUserCreatedPlaces2.add(place);
            usersCreatedPlacesMutableLiveData.postValue(oldUserCreatedPlaces2);

        }


        if (myPlacesListLiveData != null) {
            List<Place> oldMyPlaces = myPlacesListLiveData.getValue();
            oldMyPlaces.add(place);
            myPlacesListLiveData.postValue(oldMyPlaces);
        }else{
            myPlacesListLiveData = new MutableLiveData<>();
            List<Place> oldMyPlaces2 = new ArrayList<>();
            oldMyPlaces2.add(place);
            myPlacesListLiveData.postValue(oldMyPlaces2);
        }

        if (placesListLiveData != null) {
            List<Place> oldMyPlaces = placesListLiveData.getValue();
            oldMyPlaces.add(place);
            placesListLiveData.postValue(oldMyPlaces);
        }else{
            placesListLiveData = new MutableLiveData<>();
            List<Place> oldMyPlaces2 = new ArrayList<>();
            oldMyPlaces2.add(place);
            placesListLiveData.postValue(oldMyPlaces2);

        }
    }

    public MutableLiveData<Result>  getUserCreatedEvents(long lastUpdate) {
        if (usersCreatedEventsMutableLiveData == null) {
            usersCreatedEventsMutableLiveData = iRepositoryWithLiveData.getUsersCreatedEvents(lastUpdate);
        }
        return usersCreatedEventsMutableLiveData;
    }

    public MutableLiveData<List<Place>>  getUserCreatedPlaces(long lastUpdate) {
        if (usersCreatedPlacesMutableLiveData == null) {
            usersCreatedPlacesMutableLiveData = iRepositoryWithLiveData.getUsersCreatedPlaces(lastUpdate);
        }
        return usersCreatedPlacesMutableLiveData;
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

    public MutableLiveData<String> getFavoriteCategory() {
        favoriteCategoryLiveData = iRepositoryWithLiveData.getFavoriteCategory();
        return favoriteCategoryLiveData;
    }

    public MutableLiveData<Result> getFavoriteCategoryEventsLiveData() {
        favoriteCategoryEventsLiveData = iRepositoryWithLiveData.getFavoriteCategoryEvents();
        return favoriteCategoryEventsLiveData;
    }

    public MutableLiveData<Result> getFavoritePlacesLiveData(boolean isFirstLoading) {
        if (favoritePlacesListLiveData == null) {
            favoritePlacesListLiveData = iRepositoryWithLiveData.getFavoritePlaces(isFirstLoading);
        }
        return favoritePlacesListLiveData;
    }

    public MutableLiveData<Result> getEventsFromSearchLiveData(String input) {
        eventsFromSearchLiveData = iRepositoryWithLiveData.getEventsFromSearch(input);
        return eventsFromSearchLiveData;
    }

    /**
     * Updates the event status.
     *
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

    public MutableLiveData<Result> getMyEventsLiveData(boolean isFirstLoading) {
        if (myEventsListLiveData == null) {
            myEventsListLiveData = iRepositoryWithLiveData.getMyEvents(isFirstLoading);
        }
        return myEventsListLiveData;
    }

    public void deleteMyEvent(Events events) {
        iRepositoryWithLiveData.deleteMyEvent(events);

        Result myEventsResult = myEventsListLiveData.getValue();
        Result allEventsResult = eventsListLiveData.getValue();
        Result userCreatedEventsResult = usersCreatedEventsMutableLiveData.getValue();


        if (myEventsResult != null && myEventsResult.isSuccess()) {
            List<Events> oldMyEvents = ((Result.EventsResponseSuccess) myEventsResult).getData().getEventsList();
            if(oldMyEvents.contains(events)){
                oldMyEvents.remove(events);
                Log.e(TAG, "MyEventsList size:" + oldMyEvents.size());
                myEventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldMyEvents)));
            }

        }

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Events> oldAllEvents = ((Result.EventsResponseSuccess) allEventsResult).getData().getEventsList();
            if(oldAllEvents.contains(events)){
                oldAllEvents.remove(events);
                Log.e(TAG, "AllEventsList size:" + oldAllEvents.size());
                eventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldAllEvents)));
            }

        }

        if (userCreatedEventsResult != null) {
            List<Events> oldUserCreatedEvents = ((Result.EventsResponseSuccess) userCreatedEventsResult).getData().getEventsList();
            if(oldUserCreatedEvents.contains(events)){
                oldUserCreatedEvents.remove(events);
                Log.e(TAG, "userCreatedEventsList size:" + oldUserCreatedEvents.size());
                usersCreatedEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(oldUserCreatedEvents)));
            }

        }
    }

    public MutableLiveData<List<Place>> getMyPlacesLiveData(boolean isFirstLoading) {
        if (myPlacesListLiveData == null) {
            myPlacesListLiveData = iRepositoryWithLiveData.getMyPlaces(isFirstLoading);
        }
        return myPlacesListLiveData;
    }

    public void deleteMyPlace(Place place) {

        iRepositoryWithLiveData.deleteMyPlace(place);

        if (usersCreatedPlacesMutableLiveData != null) {
            List<Place> oldUserCreatedPlaces = usersCreatedPlacesMutableLiveData.getValue();
            oldUserCreatedPlaces.add(place);
            Log.e(TAG, "userCreatedEvents size: " + oldUserCreatedPlaces.size());
            usersCreatedPlacesMutableLiveData.postValue(oldUserCreatedPlaces);
        }else{
            usersCreatedPlacesMutableLiveData = new MutableLiveData<>();
            List<Place> oldUserCreatedPlaces2 = new ArrayList<>();
            oldUserCreatedPlaces2.add(place);
            usersCreatedPlacesMutableLiveData.postValue(oldUserCreatedPlaces2);

        }


        if (myPlacesListLiveData != null) {
            List<Place> oldMyPlaces = myPlacesListLiveData.getValue();
            oldMyPlaces.add(place);
            myPlacesListLiveData.postValue(oldMyPlaces);
        }else{
            myPlacesListLiveData = new MutableLiveData<>();
            List<Place> oldMyPlaces2 = new ArrayList<>();
            oldMyPlaces2.add(place);
            myPlacesListLiveData.postValue(oldMyPlaces2);
        }

        if (placesListLiveData != null) {
            List<Place> oldMyPlaces = placesListLiveData.getValue();
            oldMyPlaces.add(place);
            placesListLiveData.postValue(oldMyPlaces);
        }else{
            placesListLiveData = new MutableLiveData<>();
            List<Place> oldMyPlaces2 = new ArrayList<>();
            oldMyPlaces2.add(place);
            placesListLiveData.postValue(oldMyPlaces2);

        }
    }
}
