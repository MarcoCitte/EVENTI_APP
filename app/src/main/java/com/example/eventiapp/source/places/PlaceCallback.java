package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Place;

import java.util.List;

public interface PlaceCallback {
    void onSuccessFromLocalP(List<Place> placeList);
    void onFailureFromLocalP(Exception exception);
    void onSingleEvent(Place place);
    void onPlacesFavoriteStatusChanged(Place place, List<Place> favoritePlaces);
    void onPlacesFavoriteStatusChanged(List<Place> placeList);
    void onDeleteFavoritePlacesSuccess(List<Place> favoritePlaces);
    void onSuccessSynchronizationP();
    void onSuccessDeletionP();
}
