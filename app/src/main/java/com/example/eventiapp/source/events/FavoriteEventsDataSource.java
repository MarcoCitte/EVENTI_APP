package com.example.eventiapp.source.events;

import static com.example.eventiapp.util.Constants.FIREBASE_FAVORITE_EVENTS_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE; import static com.example.eventiapp.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventiapp.model.Events;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to get the user favorite news using Firebase Realtime Database.
 */
public class FavoriteEventsDataSource extends BaseFavoriteEventsDataSource{
    private static final String TAG = FavoriteEventsDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final String idToken;

    public FavoriteEventsDataSource(String idToken) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }

    @Override
    public void getFavoriteEvents() {


        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Events> eventsList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            Events events = ds.getValue(Events.class);
                            events.setSynchronized(true);
                            eventsList.add(events);
                        }

                        eventsCallback.onSuccessFromCloudReading(eventsList);
                    }
                });
    }

    @Override
    public void addFavoriteEvents(Events events) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).
                child(String.valueOf(events.hashCode())).
                setValue(events)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        events.setSynchronized(true);
                        eventsCallback.onSuccessFromCloudWriting(events);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        eventsCallback.onFailureFromCloud(e);
                    }
                });
    }

    @Override
    public void synchronizeFavoriteEvents(List<Events> notSynchronizedEventsList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Events> eventsList = new ArrayList<>();
                        for (DataSnapshot ds : task.getResult().getChildren()) {
                            Events event = ds.getValue(Events.class);
                            event.setSynchronized(true);
                            eventsList.add(event);
                        }

                        eventsList.addAll(notSynchronizedEventsList);

                        for (Events event : eventsList) {
                            databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                                    child(FIREBASE_FAVORITE_EVENTS_COLLECTION).
                                    child(String.valueOf(event.hashCode())).setValue(event).addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    event.setSynchronized(true);
                                                }
                                            }
                                    );
                        }
                    }
                });
    }

    @Override
    public void deleteFavoriteEvents(Events events) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).child(String.valueOf(events.hashCode())).
                removeValue().addOnSuccessListener(aVoid -> {
                    //QUI
                    events.setSynchronized(false);
                    eventsCallback.onSuccessFromCloudWriting(events);
                }).addOnFailureListener(e -> {
                    eventsCallback.onFailureFromCloud(e);
                });
    }

    @Override
    public void deleteAllFavoriteEvents() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                child(FIREBASE_FAVORITE_EVENTS_COLLECTION).removeValue().addOnSuccessListener(aVoid -> {
                    eventsCallback.onSuccessFromCloudWriting(null);
                }).addOnFailureListener(e -> {
                    eventsCallback.onFailureFromCloud(e);
                });
    }
}
