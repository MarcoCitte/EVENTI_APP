package com.example.eventiapp.source.places;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;

import java.util.List;

public interface PlaceCallback {
    void onSuccessFromLocalP(List<Place> placeList);
    void onFailureFromLocalP(Exception exception);
    void onSinglePlace(Place place);
    void onPlacesFromSearch(List<Place> places);
    void onPlacesFavoriteStatusChanged(Place place, List<Place> favoritePlaces);
    void onPlacesFavoriteStatusChanged(List<Place> placeList);
    void onDeleteFavoritePlacesSuccess(List<Place> favoritePlaces);
    void onSuccessSynchronizationP();
    void onSuccessDeletionP();


    void onSuccessFromCloudReading2(List<Place> placesList);

    void onSuccessFromCloudWriting2(Place place);

    void onFailureFromCloud2(Exception exception);

    void onSuccessFromInsertUserCreatedPlace(Place place);

    void onSuccessFromReadUserCreatedPlaces(List<Place> placesList);

    void onSuccessFromReadUserCreatedPlaceLocal(List<Place> usersCreatedPlaces);

    void onSuccessFromRemoteCurrentUserPlacesReading(List<Place> placesList);

    void onSuccessFromLocalCurrentUserPlacesReading(List<Place> myPlaces);

    void onSuccessFromLocalCurrentUserPlaceDeletion(Place place);

    void onSuccessFromRemoteCurrentUserPlaceDeletion(Place place);

    void onFailureFromCloud(Exception e);

    void onSuccessFromRemoteCurrentUserPlaceEdit(Place newPlace);

    void onSuccessFromLocalCurrentUserPlaceEdit(Place newPlace);
}
