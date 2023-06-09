package com.example.eventiapp.source.places;

import static com.example.eventiapp.util.Constants.FIREBASE_FAVORITE_PLACES_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.events.FavoriteEventsDataSource;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritePlacesDataSource extends BaseFavoritePlacesDataSource {

    private static final String TAG = FavoriteEventsDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final String idToken;

    public FavoritePlacesDataSource(String idToken) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }

    @Override
    public void getFavoritePlaces() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_PLACES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    } else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Place> placesList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Place place = ds.getValue(Place.class);
                            if (place != null) {
                                place.setSynchronized(true);
                            }
                            placesList.add(place);
                        }

                        placeCallback.onSuccessFromCloudReading2(placesList);
                    }
                });
    }

    @Override
    public void addFavoritePlace(Place place) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_PLACES_COLLECTION).
                child(String.valueOf(place.hashCode())).
                setValue(place)
                .addOnSuccessListener(aVoid -> {
                    place.setSynchronized(true);
                    placeCallback.onSuccessFromCloudWriting2(place);
                })
                .addOnFailureListener(e -> placeCallback.onFailureFromCloud2(e));
    }

    @Override
    public void synchronizeFavoritePlaces(List<Place> notSynchronizedPlacesList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_PLACES_COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Place> eventsList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Place place = ds.getValue(Place.class);
                            if (place != null) {
                                place.setSynchronized(true);
                            }
                            eventsList.add(place);
                        }

                        eventsList.addAll(notSynchronizedPlacesList);

                        for (Place place : eventsList) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                    child(FIREBASE_FAVORITE_PLACES_COLLECTION).
                                    child(String.valueOf(place.hashCode())).setValue(place).addOnSuccessListener(
                                            unused -> place.setSynchronized(true)
                                    );
                        }
                    }
                });
    }

    @Override
    public void deleteFavoritePlace(Place place) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_PLACES_COLLECTION).child(String.valueOf(place.hashCode())).
                removeValue().addOnSuccessListener(aVoid -> {
                    //QUI
                    place.setSynchronized(false);
                    placeCallback.onSuccessFromCloudWriting2(place);
                }).addOnFailureListener(e -> placeCallback.onFailureFromCloud2(e));
    }

    @Override
    public void deleteAllFavoriteEvents() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_PLACES_COLLECTION).removeValue().addOnSuccessListener(aVoid -> placeCallback.onSuccessFromCloudWriting2(null)).addOnFailureListener(e -> placeCallback.onFailureFromCloud2(e));
    }
}
