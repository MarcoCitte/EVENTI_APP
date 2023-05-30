package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;

import java.util.List;

public abstract class BaseFavoritePlacesDataSource {

    protected PlaceCallback placeCallback;

    public void setPlacesCallback(PlaceCallback placeCallback) {
        this.placeCallback = placeCallback;
    }

    public abstract void getFavoritePlaces();
    public abstract void addFavoritePlace(Place place);
    public abstract void synchronizeFavoritePlaces(List<Place> notSynchronizedPlacesList);
    public abstract void deleteFavoritePlace(Place place);
    public abstract void deleteAllFavoriteEvents();
}
