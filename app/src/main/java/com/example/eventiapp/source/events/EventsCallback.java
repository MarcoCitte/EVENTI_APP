package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;

import java.util.List;

public interface EventsCallback {
    void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate);

    void onSuccessFromRemoteJsoup(EventsApiResponse eventsApiResponse);

    void onFailureFromRemote(Exception exception);

    void onSuccessFromLocal(EventsApiResponse eventsApiResponse);

    void onFailureFromLocal(Exception exception);

    void onEventsCategory(List<Events> events);

    void onEventsPlace(List<Events> events);

    void onSingleEvent(Events event);

    void onEventsInADate(List<Events> events);

    void onEventsDates(List<String> dates);

    void onMoviesHours(String[] hours);

    void onFavoriteCategory(String category);

    void onFavoriteCategoryEvents(List<Events> events);

    void onEventsFavoriteStatusChanged(Events events, List<Events> favoriteEvents);
    public void onSuccessFromRemoteCurrentUserEventsWriting(Events events);
    public void onSuccessFromLocalCurrentUserEventDeletion(Events events);

    void onEventsFavoriteStatusChanged(List<Events> events);

    void onDeleteFavoriteEventsSuccess(List<Events> favoriteEvents);

    void onCount(int count);

    void onAllCategories(List<String> categories);

    void onCategoriesInADate(List<String> categories);

    void onCategoriesEvents(List<Events> events);

    void onEventsBetweenDates(List<Events> events);

    void onCategoryEventsBetweenDates(List<Events> events);

    void onEventsFromSearch(List<Events> events);

    void onSuccessFromCloudReading(List<Events> eventsList);

    void onSuccessFromCloudWriting(Events events);


    void onFailureFromCloud(Exception exception);

    void onSuccessSynchronization();

    void onSuccessDeletion();

    void onSuccessFromInsertUserCreatedEvent(Events events);
    void onSuccessFromReadUserCreatedEvent(List<Events> eventsList);

    void onSuccessFromReadUserCreatedEventLocal(List<Events> eventsList);

    void onSuccessFromRemoteCurrentUserEventsReading(List<Events> eventsList);

    void onSuccessFromLocalCurrentUserEventsReading(List<Events> eventsList);


    void onSuccessFromRemoteCurrentUserEventDeletion(Events events);

    void onSuccessFromRemoteCurrentUserEventEdit(Events modEvent);

    void onSuccessFromLocalCurrentUserEventsEdit(Events modEvent);
}
