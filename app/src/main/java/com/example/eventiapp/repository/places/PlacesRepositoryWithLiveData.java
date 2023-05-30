package com.example.eventiapp.repository.places;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.places.BasePlacesLocalDataSource;
import com.example.eventiapp.source.places.PlaceCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlacesRepositoryWithLiveData implements IPlacesRepositoryWithLiveData, PlaceCallback { //NON SERVE

    private static final String TAG = PlacesRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<List<Place>> allPlacesMutableLiveData;
    private final MutableLiveData<List<Place>> favoritePlacesMutableLiveData;
    private final MutableLiveData<Place> singlePlaceMutableLiveData;
    private BasePlacesLocalDataSource placesLocalDataSource;

    public PlacesRepositoryWithLiveData(BasePlacesLocalDataSource placesLocalDataSource) {
        allPlacesMutableLiveData = new MutableLiveData<>();
        favoritePlacesMutableLiveData = new MutableLiveData<>();
        singlePlaceMutableLiveData = new MutableLiveData<>();
        this.placesLocalDataSource = placesLocalDataSource;
        this.placesLocalDataSource.setPlacesCallback(this);
    }

    @Override
    public MutableLiveData<List<Place>> fetchPlaces() {
        placesLocalDataSource.getPlaces();
        return allPlacesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<Place>> getFavoritePlaces(boolean isFirstLoading) {
        if (isFirstLoading) {
            //PRENDE I BACKUP
        } else {
            placesLocalDataSource.getFavoritePlaces();
        }
        return favoritePlacesMutableLiveData;
    }

    @Override
    public MutableLiveData<Place> getSinglePlace(String id) {
        placesLocalDataSource.getSinglePlace(id);
        return singlePlaceMutableLiveData;
    }

    @Override
    public void onSuccessFromLocalP(List<Place> placeList) {
        if (allPlacesMutableLiveData.getValue() != null) {
            allPlacesMutableLiveData.postValue(placeList);
        } else {
            allPlacesMutableLiveData.postValue(Collections.emptyList());
        }
    }

    @Override
    public void onFailureFromLocalP(Exception exception) {
        exception.getMessage();
    }

    @Override
    public void onSinglePlace(Place place) {
        singlePlaceMutableLiveData.postValue(place);
    }

    @Override
    public void onPlacesFromSearch(List<Place> places) {

    }


    @Override
    public void onPlacesFavoriteStatusChanged(Place place, List<Place> favoritePlaces) {
        List<Place> allPlaces = allPlacesMutableLiveData.getValue();
        if (allPlaces != null) {
            List<Place> oldAllPlaces = allPlaces;
            if (oldAllPlaces.contains(place)) {
                oldAllPlaces.set(oldAllPlaces.indexOf(place), place);
                allPlacesMutableLiveData.postValue(allPlaces);
            }
        }
        favoritePlacesMutableLiveData.postValue(favoritePlaces);
    }

    @Override
    public void onPlacesFavoriteStatusChanged(List<Place> placeList) {
        List<Place> notSynchronizedPlacesList = new ArrayList<>();

        for (Place p : placeList) {
            if (!p.isSynchronized()) {
                notSynchronizedPlacesList.add(p);
            }
        }

        if (!notSynchronizedPlacesList.isEmpty()) {
            //BACKUP
        }

        favoritePlacesMutableLiveData.postValue(placeList);
    }

    @Override
    public void onDeleteFavoritePlacesSuccess(List<Place> favoritePlaces) {
        List<Place> allPlaces = allPlacesMutableLiveData.getValue();

        if (allPlaces != null) {
            List<Place> oldAllPlaces = allPlaces;
            for (Place p : favoritePlaces) {
                if (oldAllPlaces.contains(p)) {
                    oldAllPlaces.set(oldAllPlaces.indexOf(p), p);
                }
            }
            allPlacesMutableLiveData.postValue(allPlaces);
        }

        if (favoritePlacesMutableLiveData.getValue() != null) {
            favoritePlaces.clear();
            favoritePlacesMutableLiveData.postValue(favoritePlaces);
        }

        //backupDataSource.deleteAllFavoriteNews();
    }

    @Override
    public void onSuccessSynchronizationP() {
        Log.d(TAG, "Places synchronized from remote");
    }

    @Override
    public void onSuccessDeletionP() {

    }

    @Override
    public void onSuccessFromCloudReading2(List<Place> placesList) {

    }

    @Override
    public void onSuccessFromCloudWriting2(Place place) {

    }

    @Override
    public void onFailureFromCloud2(Exception exception) {

    }
}
