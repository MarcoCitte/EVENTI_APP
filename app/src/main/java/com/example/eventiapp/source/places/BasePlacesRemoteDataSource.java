package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Place;

public abstract class BasePlacesRemoteDataSource {

    public static PlaceCallback placeCallback;

    public void setEventsCallback(PlaceCallback placeCallback){
        this.placeCallback=placeCallback;
    }


    public abstract void insertPlace(Place place);

    public abstract void getUsersCreatedPlaces();
}
