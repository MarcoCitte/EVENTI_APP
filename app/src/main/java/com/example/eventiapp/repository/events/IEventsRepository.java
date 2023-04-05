package com.example.eventiapp.repository.events;

import com.example.eventiapp.model.Events;

public interface IEventsRepository {

    void fetchEvents(String country, String location, String date, int limit, long lastUpdate);

    void updateEvents(Events events);

    void getFavoriteEvents();

    void deleteFavoriteEvents();
}
