package com.example.eventiapp.repository.events;

import com.example.eventiapp.model.Events;

import java.util.List;

public interface EventsResponseCallback {
    void onSuccess(List<Events> eventsList);
    void onFailure(String errorMessage);
    void onEventsFavoriteStatusChanged(Events events);
}
