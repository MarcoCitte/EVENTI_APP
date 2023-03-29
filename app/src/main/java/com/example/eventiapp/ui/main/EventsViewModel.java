package com.example.eventiapp.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepository;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;

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

    public EventsViewModel(IEventsRepositoryWithLiveData iEventsRepository) {
        this.iEventsRepositoryWithLiveData = iEventsRepository;
        this.page = 1;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    public MutableLiveData<Result> getEvents(String country, long lastUpdate){
      if(eventsListLiveData==null){
          iEventsRepositoryWithLiveData.fetchEvents(country,lastUpdate);
      }
      return eventsListLiveData;
    }

    public MutableLiveData<Result> getFavoriteEventsLiveData(boolean isFirstLoading) {
        if (favoriteEventsListLiveData == null) {
            iEventsRepositoryWithLiveData.getFavoriteEvents(isFirstLoading);
        }
        return favoriteEventsListLiveData;
    }

    public void updateEvents(Events events) {
        iEventsRepositoryWithLiveData.updateEvents(events);
    }

    public void fetchEvents(String country) {
        iEventsRepositoryWithLiveData.fetchEvents(country);
    }

    private void getFavoriteEvents(boolean firstLoading) {
        favoriteEventsListLiveData = iEventsRepositoryWithLiveData.getFavoriteEvents(firstLoading);
    }

    public void removeFromFavorite(Events events) {
        iEventsRepositoryWithLiveData.updateEvents(events);
    }

    public void deleteAllFavoriteEvents() {
        iEventsRepositoryWithLiveData.deleteFavoriteEvents();
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
