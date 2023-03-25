package com.example.eventiapp.repository.events;

import static com.example.eventiapp.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.eventiapp.util.Constants.FRESH_TIMEOUT;
import static com.example.eventiapp.util.Constants.TOKEN_API_VALUE;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.util.ServiceLocator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRepository implements IEventsRepository{

    private static final String TAG = EventsRepository.class.getSimpleName();

    private final Application application;
    private final EventsApiService eventsApiService;
    //private final EventsDao eventsDao; //ROOM
    private final EventsResponseCallback eventsResponseCallback;

    public EventsRepository(Application application, EventsResponseCallback eventsResponseCallback) {
        this.application = application;
        this.eventsApiService = ServiceLocator.getInstance().getEventsApiService();
        //EventsRoomDatabase eventsRoomDatabase = ServiceLocator.getInstance().getEventsDao(application);
        //this.eventsDao = eventsRoomDatabase.eventsDao();
        this.eventsResponseCallback = eventsResponseCallback;
    }

    @Override
    public void fetchEvents(String country,long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the events from the Web Service if the last download
        // of the events has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Call<EventsApiResponse> eventsResponseCall = eventsApiService.getEvents(country,
                    CONTENT_TYPE_VALUE, TOKEN_API_VALUE);

            eventsResponseCall.enqueue(new Callback<EventsApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<EventsApiResponse> call,
                                       @NonNull Response<EventsApiResponse> response) {

                    if (response.body() != null && response.isSuccessful() &&
                            !response.body().getStatus().equals("error")) {
                        List<Events> eventsList = response.body().getEventsList();
                        //saveDataInDatabase(eventsList); //ROOM
                    } else {
                        eventsResponseCallback.onFailure("ERRORE");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EventsApiResponse> call, @NonNull Throwable t) {
                    eventsResponseCallback.onFailure(t.getMessage());
                }
            });
        } else {
            Log.d(TAG, "LETTO DA DATABASE LOCALE");
            //readDataFromDatabase(lastUpdate); //ROOM
        }
    }

    @Override
    public void updateEvents(Events events) {

    }

    @Override
    public void getFavoriteEvents() {

    }

    @Override
    public void deleteFavoriteEvents() {

    }
}
