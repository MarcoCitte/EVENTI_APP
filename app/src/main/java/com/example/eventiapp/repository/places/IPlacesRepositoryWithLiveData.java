package com.example.eventiapp.repository.places;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;

import java.util.List;

public interface IPlacesRepositoryWithLiveData { //NON SERVE

    MutableLiveData<List<Place>> fetchPlaces();

    MutableLiveData<List<Place>> getFavoritePlaces(boolean isFirstLoading);

    MutableLiveData<Place> getSinglePlace(String id);
}
