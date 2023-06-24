package com.example.eventiapp.source.places;

import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_EVENTS_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_PLACES_COLLECTION;

import android.util.Log;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.events.MyEventsDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyPlacesDataSource extends BaseMyPlacesDataSource{

    private static final String TAG = MyPlacesDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    private final String email;

    public MyPlacesDataSource(String email) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.email = email;

    }

    @Override
    public void getMyPlaces() {
        databaseReference.child(FIREBASE_USERS_CREATED_PLACES_COLLECTION)
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Place> placesList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            Place place = ds.getValue(Place.class);
                            if(place.getCreatorEmail().equals(email)){
                                place.setSynchronized(true);
                                placesList.add(place);
                            }
                        }

                        placesCallback.onSuccessFromRemoteCurrentUserPlacesReading(placesList);
                    }
                });
    }



    @Override
    public void synchronizeMyPlaces(List<Place> notSynchronizedEventsList) {

    }

    @Override
    public void deleteMyPlace(Place place) {

    }

    @Override
    public void deleteAllMyPlaces() {

    }
}
