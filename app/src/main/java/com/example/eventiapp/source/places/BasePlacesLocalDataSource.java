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

    public abstract void getPlacesFromSearch(String input);

    public abstract void getFavoritePlaces();

    public abstract void getSinglePlace(String id);

    public abstract void getSinglePlaceByName(String name);

    public abstract void updatePlaces(Place place);

    public abstract void deleteFavoritePlaces();

    public abstract void insertPlaces(List<Place> placeList);

    public abstract void deleteAll();

    public abstract void getUsersCreatedPlaces();

    public abstract void getMyPlaces();

    public abstract void deleteMyPlace(Place place);

    public abstract void editPlace(Place oldPlace, Place newPlace);
}
