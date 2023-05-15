package com.example.eventiapp.source.events;

public abstract class BaseEventsRemoteDataSource {

    public static EventsCallback eventsCallback;

    public void setEventsCallback(EventsCallback eventsCallback){
        this.eventsCallback=eventsCallback;
    }

    public abstract void getEvents(String country, String location, String date, String categories, String sort, int limit);

    public abstract void getEventsFromJsoup();
}
