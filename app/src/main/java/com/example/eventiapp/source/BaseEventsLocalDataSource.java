package com.example.eventiapp.source;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;

import java.util.List;

public abstract class BaseEventsLocalDataSource {

    protected EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback) {
        this.eventsCallback = eventsCallback;
    }

    public abstract void getEvents();

    public abstract void getFavoriteEvents();

    public abstract void getCategoryEvents(String category);

    public abstract void getSingleEvent(long id);

    public abstract void getPlaceEvent(String id);

    public abstract void getEventsDates(String name);

    public abstract void updateEvents(Events events);

    public abstract void deleteFavoriteEvents();

    public abstract void insertEvents(EventsApiResponse eventsApiResponse);

    public abstract void insertEvents(List<Events> eventsList);

    public abstract void deleteAll();
}
