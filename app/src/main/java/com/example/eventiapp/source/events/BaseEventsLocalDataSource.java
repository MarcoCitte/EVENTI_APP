package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Place;

import java.util.List;

public abstract class BaseEventsLocalDataSource {

    protected EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback) {
        this.eventsCallback = eventsCallback;
    }

    public abstract void getEvents();

    public abstract void getEventsFromADate(String date);

    public abstract void getFavoriteEvents();

    public abstract void getFavoriteCategory();

    public abstract void getFavoriteCategoryEvents();

    public abstract void getCategoryEvents(String category);

    public abstract void getSingleEvent(long id);

    public abstract void getPlaceEvent(String id);

    public abstract void getEventsInADate(String date);

    public abstract void getAllCategories();

    public abstract void getCategoriesInADate(String date);

    public abstract void getCategoriesEvents(List<String> categories);

    public abstract void getEventsBetweenDates(String startDate, String endDate);

    public abstract void getCategoryEventsBetweenDates(String startDate, String endDate, List<String> categories);

    public abstract void getEventsDates(String name);

    public abstract void getMoviesHours(String name);

    public abstract void getEventsFromSearch(String input);

    public abstract void updateEvents(Events events);

    public abstract void deleteMyEvents(Events events);


    public abstract void deleteFavoriteEvents();

    public abstract void insertEvents(EventsApiResponse eventsApiResponse);

    public abstract void insertEvents(List<Events> eventsList);

    public abstract void getCount();

    public abstract void deleteAll();

    public abstract void getUsersCreatedEvents();

    public abstract void getMyEvents();

    public abstract void editEvent(Events oldEvent, Events newEvent);

}
