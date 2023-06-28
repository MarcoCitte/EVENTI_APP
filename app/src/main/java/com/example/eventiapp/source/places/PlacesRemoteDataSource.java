package com.example.eventiapp.source.places;

import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_PLACES_COLLECTION;

import android.util.Log;


import com.example.eventiapp.model.Place;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class PlacesRemoteDataSource extends BasePlacesRemoteDataSource{

    private static final String TAG = PlacesRemoteDataSource.class.getSimpleName();


    private final DatabaseReference databaseReference;

    public PlacesRemoteDataSource() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
    }


    @Override
    public void insertPlace(Place place) {
        databaseReference.child(FIREBASE_USERS_CREATED_PLACES_COLLECTION).child(String.valueOf(place.hashCode())).
                setValue(place).addOnSuccessListener(aVoid -> {
                    //events.setSynchronized(true);
                    placeCallback.onSuccessFromInsertUserCreatedPlace(place);
                })
                .addOnFailureListener(e -> placeCallback.onFailureFromCloud2(e));
    }

    @Override
    public void getUsersCreatedPlaces() {
        databaseReference.child(FIREBASE_USERS_CREATED_PLACES_COLLECTION).get().addOnCompleteListener(task -> {
            Log.e(TAG, "Dentro il remote");
            if (!task.isSuccessful()) {
                Log.d(TAG, "Error getting data", task.getException());
            }
            else {
                Log.d(TAG, "Successful read: " + task.getResult().getValue());

                List<Place> placesList = new ArrayList<>();
                for(DataSnapshot ds : task.getResult().getChildren()) {
                    Place place = ds.getValue(Place.class);
                    if (place != null) {
                        place.setSynchronized(true);
                    }
                    placesList.add(place);
                }
                placeCallback.onSuccessFromReadUserCreatedPlaces(placesList);
            }
        });
    }
}
