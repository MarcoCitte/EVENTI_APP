package com.example.eventiapp.source;

public abstract class BaseEventsRemoteDataSource {

    protected static EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback){
        this.eventsCallback=eventsCallback;
    }

    public abstract void getEvents(String country, String location, String date, String sort, int limit);

    public abstract void getEventsFromJsoup();
}
