package com.example.eventiapp.source;

import static com.example.eventiapp.util.Constants.API_KEY_ERROR;
import static com.example.eventiapp.util.Constants.CONTENT_TYPE_VALUE;
import static com.example.eventiapp.util.Constants.RETROFIT_ERROR;

import android.util.Log;

import androidx.annotation.NonNull;
import android.app.Application;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.util.ServiceLocator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsRemoteDataSource extends BaseEventsRemoteDataSource{

    private final EventsApiService eventsApiService;
    private final String apiKey;

    public EventsRemoteDataSource(String apiKey) {
        this.eventsApiService = ServiceLocator.getInstance().getEventsApiService();
        this.apiKey = apiKey;
    }

    @Override
    public void getEvents(String country, String location, String date, String sort, int limit) {
        Call<EventsApiResponse> eventsResponseCall = eventsApiService.getEvents(country,location,date,sort,limit,
                apiKey,CONTENT_TYPE_VALUE);


        eventsResponseCall.enqueue(new Callback<EventsApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<EventsApiResponse> call,
                                   @NonNull Response<EventsApiResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    Log.i("RESPONSE BODY: ",response.body() + " FINE ");
                    eventsCallback.onSuccessFromRemote(response.body(),System.currentTimeMillis());
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

}
