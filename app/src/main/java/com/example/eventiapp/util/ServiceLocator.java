package com.example.eventiapp.util;

import android.app.Application;

import com.example.eventiapp.R;
import com.example.eventiapp.database.EventsRoomDatabase;
import com.example.eventiapp.repository.events.EventsRepositoryWithLiveData;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.BaseEventsLocalDataSource;
import com.example.eventiapp.source.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.EventsLocalDataSource;
import com.example.eventiapp.source.EventsRemoteDataSource;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

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

    public EventsRoomDatabase getEventsDao(Application application) {
        return EventsRoomDatabase.getDatabase(application);
    }

    public IEventsRepositoryWithLiveData getEventsRepository(Application application) {
        BaseEventsRemoteDataSource eventsRemoteDataSource;
        BaseEventsLocalDataSource eventsLocalDataSource;
        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil=new DataEncryptionUtil(application);

        eventsRemoteDataSource = new EventsRemoteDataSource(application.getString(R.string.events_api_key));
        eventsLocalDataSource=new EventsLocalDataSource(getEventsDao(application),sharedPreferencesUtil,dataEncryptionUtil);

        return new EventsRepositoryWithLiveData(eventsRemoteDataSource,eventsLocalDataSource);
    }
}
