package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Place;

import java.util.List;

public abstract class BaseMyPlacesDataSource {

    protected PlaceCallback placesCallback;

    public void setMyPlacesCallback(PlaceCallback placesCallback) {
        this.placesCallback = placesCallback;
    }

    public abstract void getMyPlaces();
    public abstract void synchronizeMyPlaces(List<Place> notSynchronizedEventsList);
    public abstract void deleteMyPlace(Place place);
    public abstract void deleteAllMyPlaces();
}
