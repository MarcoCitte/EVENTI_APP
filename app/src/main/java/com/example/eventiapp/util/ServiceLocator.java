package com.example.eventiapp.util;

import android.app.Application;

import com.example.eventiapp.R;
import com.example.eventiapp.repository.events.EventsRepositoryWithLiveData;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.EventsRemoteDataSource;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    /**
     * Returns an instance of ServiceLocator class.
     *
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    public EventsApiService getEventsApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.EVENTS_API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(EventsApiService.class);
    }

    public IEventsRepositoryWithLiveData getEventsRepository(Application application) {
        BaseEventsRemoteDataSource eventsRemoteDataSource;
        eventsRemoteDataSource = new EventsRemoteDataSource(application.getString(R.string.events_api_key));

        return new EventsRepositoryWithLiveData(eventsRemoteDataSource);
    }
}
