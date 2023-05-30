package com.example.eventiapp.util;

import static com.example.eventiapp.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.ID_TOKEN;

import android.app.Application;
import android.location.Geocoder;

import com.example.eventiapp.R;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.repository.user.UserRepository;
import com.example.eventiapp.database.RoomDatabase;
import com.example.eventiapp.repository.events.RepositoryWithLiveData;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.service.EventsApiService;
import com.example.eventiapp.source.events.BaseFavoriteEventsDataSource;
import com.example.eventiapp.source.events.FavoriteEventsDataSource;
import com.example.eventiapp.source.places.BaseFavoritePlacesDataSource;
import com.example.eventiapp.source.places.FavoritePlacesDataSource;
import com.example.eventiapp.source.user.BaseUserAuthenticationRemoteDataSource;
import com.example.eventiapp.source.user.BaseUserDataRemoteDataSource;
import com.example.eventiapp.source.user.UserAuthenticationRemoteDataSource;
import com.example.eventiapp.source.user.UserDataRemoteDataSource;
import com.example.eventiapp.source.events.BaseEventsLocalDataSource;
import com.example.eventiapp.source.events.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.events.EventsLocalDataSource;
import com.example.eventiapp.source.events.EventsRemoteDataSource;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.source.places.BasePlacesLocalDataSource;
import com.example.eventiapp.source.places.PlacesLocalDataSource;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

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

    public RoomDatabase getDao(Application application) {
        return RoomDatabase.getDatabase(application);
    }

    public IRepositoryWithLiveData getRepository(Application application) {
        BaseEventsRemoteDataSource eventsRemoteDataSource;
        BaseEventsLocalDataSource eventsLocalDataSource;
        BasePlacesLocalDataSource placesLocalDataSource;
        PlaceDetailsSource placeDetailsSource;
        BaseFavoriteEventsDataSource favoriteEventsDataSource;
        BaseFavoritePlacesDataSource favoritePlacesDataSource;
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        Places.initialize(application, application.getString(R.string.maps_api_key));
        PlacesClient placesClient = Places.createClient(application);
        Geocoder geocoder = new Geocoder(application, Locale.getDefault());

        eventsRemoteDataSource = new EventsRemoteDataSource(application.getString(R.string.events_api_key));
        eventsLocalDataSource = new EventsLocalDataSource(getDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        placesLocalDataSource = new PlacesLocalDataSource(getDao(application), sharedPreferencesUtil, dataEncryptionUtil);
        placeDetailsSource = new PlaceDetailsSource(placesClient,geocoder);

        try {
            favoriteEventsDataSource = new FavoriteEventsDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN
                    )
            );
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }

        try {
            favoritePlacesDataSource = new FavoritePlacesDataSource(dataEncryptionUtil.
                    readSecretDataWithEncryptedSharedPreferences(
                            ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN
                    )
            );
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }

        return new RepositoryWithLiveData(eventsRemoteDataSource, eventsLocalDataSource, placesLocalDataSource, placeDetailsSource, favoriteEventsDataSource, favoritePlacesDataSource);
    }

    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);

        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        BaseEventsLocalDataSource newsLocalDataSource =
                new EventsLocalDataSource(getDao(application), sharedPreferencesUtil,
                        dataEncryptionUtil);

        return new UserRepository(userRemoteAuthenticationDataSource,
                userDataRemoteDataSource, newsLocalDataSource);
    }
}
