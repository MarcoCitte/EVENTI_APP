package com.example.eventiapp.ui.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepository;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;

import java.util.List;

public class EventsViewModel extends ViewModel {

    private static final String TAG = EventsViewModel.class.getSimpleName();

    private final IEventsRepositoryWithLiveData iEventsRepositoryWithLiveData;
    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> eventsListLiveData;
    private MutableLiveData<Result> favoriteEventsListLiveData;
    private MutableLiveData<Result> categoryEventsLiveData;
    private MutableLiveData<Result> placeEventsLiveData;
    private MutableLiveData<Result> eventLiveData;

    public EventsViewModel(IEventsRepositoryWithLiveData iEventsRepositoryWithLiveData) {
        this.iEventsRepositoryWithLiveData = iEventsRepositoryWithLiveData;
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

    public MutableLiveData<Result> getFavoriteEventsLiveData(boolean isFirstLoading) {
        if (favoriteEventsListLiveData == null) {
            iEventsRepositoryWithLiveData.getFavoriteEvents(isFirstLoading);
        }
        return favoriteEventsListLiveData;
    }

    public MutableLiveData<Result> getCategoryEventsLiveData(String category) {
        if (categoryEventsLiveData == null) {
            categoryEventsLiveData = iEventsRepositoryWithLiveData.getCategoryEvents(category);
        }
        return categoryEventsLiveData;
    }

    public MutableLiveData<Result> getPlaceEventsLiveData(String id) {
        if(placeEventsLiveData == null) {
            return iEventsRepositoryWithLiveData.getPlaceEvents(id);
        }
        return placeEventsLiveData;
    }

    public MutableLiveData<Result> getSingleEvent(long id) {
        eventLiveData = iEventsRepositoryWithLiveData.getSingleEvent(id);
        return eventLiveData;
    }

    public MutableLiveData<List<String>> getEventsDates(String name){
        return iEventsRepositoryWithLiveData.getEventsDates(name);
    }

    public void deleteEvents() {
        iEventsRepositoryWithLiveData.deleteEvents();
    }


    public void fetchEvents(String country, String location, String date, String sort, int limit) {
        iEventsRepositoryWithLiveData.fetchEvents(country, location, date, sort, limit);
    }

    public void fetchEvents(String country, String location, String date, String sort, int limit, long lastUpdate) {
        eventsListLiveData = iEventsRepositoryWithLiveData.fetchEvents(country, location, date, sort, limit, lastUpdate);
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

}
