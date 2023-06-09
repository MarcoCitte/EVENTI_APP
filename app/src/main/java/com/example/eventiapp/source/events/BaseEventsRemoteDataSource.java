package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;

public abstract class BaseEventsRemoteDataSource {

    public static EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback){
        BaseEventsRemoteDataSource.eventsCallback =eventsCallback;
    }

    public abstract void getEvents(String country, String location, String date, String categories, String sort, int limit);

    public abstract void getEventsFromJsoup();

    public abstract void insertEvents(Events events);

    public abstract void getUsersCreatedEvents();

}
