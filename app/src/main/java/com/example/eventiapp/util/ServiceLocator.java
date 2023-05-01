package com.example.eventiapp.util;

import android.app.Application;
import android.location.Geocoder;

import com.example.eventiapp.R;
import com.example.eventiapp.database.RoomDatabase;
import com.example.eventiapp.repository.events.RepositoryWithLiveData;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.events.BaseEventsLocalDataSource;
import com.example.eventiapp.source.events.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.events.EventsLocalDataSource;
import com.example.eventiapp.source.events.EventsRemoteDataSource;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.source.places.BasePlacesLocalDataSource;
import com.example.eventiapp.source.places.PlacesLocalDataSource;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Locale;

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

    public RoomDatabase getDao(Application application) {
        return RoomDatabase.getDatabase(application);
    }

    public IRepositoryWithLiveData getRepository(Application application) {
        BaseEventsRemoteDataSource eventsRemoteDataSource;
        BaseEventsLocalDataSource eventsLocalDataSource;
        BasePlacesLocalDataSource placesLocalDataSource;
        PlaceDetailsSource placeDetailsSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        Places.initialize(application, application.getString(R.string.maps_api_key));
        PlacesClient placesClient = Places.createClient(application);
        Geocoder geocoder = new Geocoder(application, Locale.getDefault());

        eventsRemoteDataSource = new EventsRemoteDataSource(application.getString(R.string.events_api_key));
        eventsLocalDataSource = new EventsLocalDataSource(getDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        placesLocalDataSource = new PlacesLocalDataSource(getDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        placeDetailsSource = new PlaceDetailsSource(placesClient,geocoder);
        return new RepositoryWithLiveData(eventsRemoteDataSource, eventsLocalDataSource, placesLocalDataSource, placeDetailsSource);
    }
}
