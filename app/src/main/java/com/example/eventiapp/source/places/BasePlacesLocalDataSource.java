package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.events.EventsCallback;

import java.util.List;

public abstract class BasePlacesLocalDataSource {
    protected PlaceCallback placeCallback;

    public void setPlacesCallback(PlaceCallback placeCallback) {
        this.placeCallback = placeCallback;
    }

    public abstract void getPlaces();

    public abstract void getFavoritePlaces();

    public abstract void getSinglePlace(String id);

    public abstract void getSinglePlaceByName(String name);

    public abstract void updatePlaces(Place place);

    public abstract void deleteFavoritePlaces();

    public abstract void insertPlaces(List<Place> placeList);

    public abstract void deleteAll();
}
