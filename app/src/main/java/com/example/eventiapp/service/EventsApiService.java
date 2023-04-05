package com.example.eventiapp.service;

import static com.example.eventiapp.util.Constants.CLIENT_ID;
import static com.example.eventiapp.util.Constants.CONTENT_TYPE;
import static com.example.eventiapp.util.Constants.EVENTS_COUNTRY;
import static com.example.eventiapp.util.Constants.EVENTS_ENDPOINT;
import static com.example.eventiapp.util.Constants.EVENTS_LIMIT;
import static com.example.eventiapp.util.Constants.EVENTS_LOCATION;
import static com.example.eventiapp.util.Constants.EVENTS_START;
import static com.example.eventiapp.util.Constants.TOKEN_API;

import com.example.eventiapp.model.EventsApiResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventsApiService {
    @GET(EVENTS_ENDPOINT)
    Call<EventsApiResponse> getEvents(
            @Query(EVENTS_COUNTRY) String country,
            @Query(EVENTS_LOCATION) String location,
            @Query(EVENTS_START) String date,
            @Query(EVENTS_LIMIT) int limit,
            @Header(TOKEN_API) String authorization,
            @Header(CONTENT_TYPE) String contentType);
}
