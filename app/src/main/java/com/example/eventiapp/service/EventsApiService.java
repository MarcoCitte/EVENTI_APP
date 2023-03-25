package com.example.eventiapp.service;

import static com.example.eventiapp.util.Constants.CLIENT_ID;
import static com.example.eventiapp.util.Constants.CONTENT_TYPE;
import static com.example.eventiapp.util.Constants.TOKEN_API;
import static com.example.eventiapp.util.Constants.TOP_HEADLINES_COUNTRY_PARAMETER;
import static com.example.eventiapp.util.Constants.TOP_HEADLINES_ENDPOINT;

import com.example.eventiapp.model.EventsApiResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface EventsApiService {
    @GET(TOP_HEADLINES_ENDPOINT)
    Call<EventsApiResponse> getEvents(
            @Query(TOP_HEADLINES_COUNTRY_PARAMETER) String country,
            @Header(CONTENT_TYPE) String contentType,
            @Header(TOKEN_API) String authorization);
}
