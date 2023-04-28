package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;

import java.util.List;

public interface EventsCallback {
    void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(EventsApiResponse eventsApiResponse);
    void onFailureFromLocal(Exception exception);
    void onEventsCategory(List<Events> events);
    void onEventsPlace(List <Events> events);
    void onSingleEvent(Events event);
    void onEventsDates(List<String> dates);
    void onMoviesHours(String[] hours);
    void onEventsFavoriteStatusChanged(Events events, List<Events> favoriteEvents);
    void onEventsFavoriteStatusChanged(List<Events> events);
    void onDeleteFavoriteEventsSuccess(List<Events> favoriteEvents);
    void onCount(int count);
    void onSuccessFromCloudReading(List<Events> eventsList);
    void onSuccessFromCloudWriting(Events events);
    void onFailureFromCloud(Exception exception);
    void onSuccessSynchronization();
    void onSuccessDeletion();
}
