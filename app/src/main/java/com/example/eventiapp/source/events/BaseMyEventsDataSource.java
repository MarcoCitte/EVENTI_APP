package com.example.eventiapp.source.events;

import com.example.eventiapp.model.Events;

public abstract class  BaseMyEventsDataSource {
    protected EventsCallback eventsCallback;

    public void setMyEventsCallback(EventsCallback eventsCallback) {
        this.eventsCallback = eventsCallback;
    }

    public abstract void getMyEvents();
    //public abstract void synchronizeMyEvents(List<Events> notSynchronizedEventsList);
    public abstract void deleteMyEvents(Events events);
    public abstract void deleteAllMyEvents();

    public abstract void editEvent(Events oldEvent, Events newEvent);
}
