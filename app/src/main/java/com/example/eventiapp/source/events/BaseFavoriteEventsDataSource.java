package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;

import java.util.List;

public abstract class BaseFavoriteEventsDataSource {
    protected EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback) {
        this.eventsCallback = eventsCallback;
    }

    public abstract void getFavoriteEvents();
    public abstract void addFavoriteEvents(Events events);
    public abstract void synchronizeFavoriteEvents(List<Events> notSynchronizedEventsList);
    public abstract void deleteFavoriteEvents(Events events);
    public abstract void deleteAllFavoriteEvents();
}
