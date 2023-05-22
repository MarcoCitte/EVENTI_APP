package com.example.eventiapp.source.events;

import static com.example.eventiapp.util.Constants.API_KEY_ERROR;
import static com.example.eventiapp.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.eventiapp.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.jsoup.JsoupDataSource;
import com.example.eventiapp.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRemoteDataSource extends BaseEventsRemoteDataSource implements JsoupDataSource.OnPostExecuteListener {

    private final EventsApiService eventsApiService;
    private final String apiKey;

    private Response<EventsApiResponse> eventsApiResponse;

    public EventsRemoteDataSource(String apiKey) {
        this.eventsApiService = ServiceLocator.getInstance().getEventsApiService();
        this.apiKey = apiKey;
    }

    @Override
    public void getEvents(String country, String location, String date, String categories, String sort, int limit) {
        Call<EventsApiResponse> eventsResponseCall = eventsApiService.getEvents(country,location,date, categories, sort,limit,
                apiKey,CONTENT_TYPE_VALUE);
        getEventsFromJsoup(); //RICHIAMA POI onSuccessFromRemoteJsoup

        eventsResponseCall.enqueue(new Callback<EventsApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsApiResponse> call,
                                   @NonNull Response<EventsApiResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                   eventsApiResponse=response;
                } else {
                    eventsCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsApiResponse> call, @NonNull Throwable t) {
                eventsCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }


    @Override
    public void getEventsFromJsoup() {
       JsoupDataSource jsoupDataSource=new JsoupDataSource();
       jsoupDataSource.setOnPostExecuteListener(this);
       jsoupDataSource.execute();
    }

    @Override
    public void onPostExecuted(List<Events> eventsList) {
        List<Events> updatedEventsList = new ArrayList<>(eventsApiResponse.body().getEventsList());
        updatedEventsList.addAll(eventsList);

        EventsApiResponse updatedResponse = new EventsApiResponse(updatedEventsList);

        eventsCallback.onSuccessFromRemote(updatedResponse, System.currentTimeMillis());
        Log.i("RESPONSE", String.valueOf(updatedResponse.getCount()));
    }
}
