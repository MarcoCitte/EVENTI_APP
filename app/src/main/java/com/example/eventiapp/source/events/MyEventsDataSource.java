package com.example.eventiapp.source.events;

import static com.example.eventiapp.util.Constants.FIREBASE_FAVORITE_EVENTS_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_REALTIME_DATABASE;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_COLLECTION;
import static com.example.eventiapp.util.Constants.FIREBASE_USERS_CREATED_EVENTS_COLLECTION;

import android.util.Log;

import com.example.eventiapp.model.Events;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyEventsDataSource extends BaseMyEventsDataSource {

    private static final String TAG = MyEventsDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;

    private final String email;


    public MyEventsDataSource(String email) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.email = email;

    }

    @Override
    public void getMyEvents() {
        databaseReference.child(FIREBASE_USERS_CREATED_EVENTS_COLLECTION)
                .get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Error getting data", task.getException());
                    }
                    else {
                        Log.d(TAG, "Successful read: " + task.getResult().getValue());

                        List<Events> eventsList = new ArrayList<>();
                        for(DataSnapshot ds : task.getResult().getChildren()) {
                            Events events = ds.getValue(Events.class);
                            if(events.getCreatorEmail().equals(email)){
                                events.setSynchronized(true);
                                eventsList.add(events);
                            }
                            events.setSynchronized(true);
                            eventsList.add(events);
                        }

                        eventsCallback.onSuccessFromRemoteCurrentUserEventsReading(eventsList);
                    }
                });
    }



    @Override
    public void synchronizeMyEvents(List<Events> notSynchronizedEventsList) {

    }

    @Override
    public void deleteMyEvents(Events events) {
        databaseReference.child(FIREBASE_USERS_CREATED_EVENTS_COLLECTION)
                .child(String.valueOf(events.hashCode())).
                removeValue().addOnSuccessListener(aVoid -> {
                    //QUI
                    events.setSynchronized(false);
                    eventsCallback.onSuccessFromRemoteCurrentUserEventsWriting(events);
                }).addOnFailureListener(e -> {
                    eventsCallback.onFailureFromCloud(e);
                });
    }

    @Override
    public void deleteAllMyEvents() {

    }
}
