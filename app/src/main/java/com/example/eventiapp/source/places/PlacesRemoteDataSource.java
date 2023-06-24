package com.example.eventiapp.source.places;

import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_EVENTS_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_PLACES_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.events.EventsRemoteDataSource;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

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
                setValue(place).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //events.setSynchronized(true);
                        placeCallback.onSuccessFromInsertUserCreatedPlace(place);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        placeCallback.onFailureFromCloud2(e);
                    }
                });
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
                    place.setSynchronized(true);
                    placesList.add(place);
                }
                placeCallback.onSuccessFromReadUserCreatedPlaces(placesList);
            }
        });
    }
}
