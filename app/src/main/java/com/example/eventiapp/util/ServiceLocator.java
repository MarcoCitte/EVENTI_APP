package com.example.eventiapp.util;

import com.example.eventiapp.service.EventsApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public EventsApiService getEventsApiService(){
        Retrofit retrofit=new Retrofit.Builder().baseUrl(Constants.EVENTS_API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(EventsApiService.class);
    }
}
