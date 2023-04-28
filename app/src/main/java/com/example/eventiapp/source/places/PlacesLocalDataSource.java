package com.example.eventiapp.source.places;

import static com.example.eventiapp.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.eventiapp.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import com.example.eventiapp.database.PlaceDao;
import com.example.eventiapp.database.RoomDatabase;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.util.DataEncryptionUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class PlacesLocalDataSource extends BasePlacesLocalDataSource {

    private final PlaceDao placeDao;
    private final SharedPreferencesUtil sharedPreferences;
    private final DataEncryptionUtil dataEncryptionUtil;

    public PlacesLocalDataSource(RoomDatabase roomDatabase, SharedPreferencesUtil sharedPreferences, DataEncryptionUtil dataEncryptionUtil) {
        this.placeDao = roomDatabase.placeDao();
        this.sharedPreferences = sharedPreferences;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getPlaces() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Place> placeList = new ArrayList<>(placeDao.getAll());
            placeCallback.onSuccessFromLocalP(placeList);
        });
    }

    @Override
    public void getFavoritePlaces() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Place> favoritePlaces = placeDao.getFavoritePlaces();
            placeCallback.onPlacesFavoriteStatusChanged(favoritePlaces);
        });
    }

    @Override
    public void getSinglePlace(String id) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            Place place = placeDao.getPlace(id);
            placeCallback.onSingleEvent(place);
        });
    }

    @Override
    public void updatePlaces(Place place) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            if (place != null) {
                int rowUpdatedCounter = placeDao.updateSingleFavoritePlace(place);
                if (rowUpdatedCounter == 1) {
                    Place updatedPlace = placeDao.getPlace(place.getId());
                    placeCallback.onPlacesFavoriteStatusChanged(updatedPlace, placeDao.getFavoritePlaces());
                } else {
                    placeCallback.onFailureFromLocalP(new Exception("ERRORE"));
                }
            } else {
                List<Place> allPlaces = placeDao.getAll();
                for (Place p : allPlaces) {
                    p.setSynchronized(false);
                    placeDao.updateSingleFavoritePlace(p);
                }
            }
        });
    }

    @Override
    public void deleteFavoritePlaces() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Place> favoritePlaces = placeDao.getFavoritePlaces();
            for (Place p : favoritePlaces) {
                p.setFavorite(false);
            }
            int updatedRowsNumber = placeDao.updateListFavoritePlace(favoritePlaces);
            if (updatedRowsNumber == favoritePlaces.size()) {
                placeCallback.onDeleteFavoritePlacesSuccess(favoritePlaces);
            } else {
                placeCallback.onFailureFromLocalP(new Exception("ERRORE"));
            }
        });
    }

    @Override
    public void insertPlaces(List<Place> placeList) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            if (placeList != null) {
                List<Place> allPlaces = placeDao.getAll();

                for (Place p : allPlaces) {
                    if (placeList.contains(p)) {
                        p.setSynchronized(true);
                        placeList.set(placeList.indexOf(p), p);
                    }
                }
                placeCallback.onSuccessFromLocalP(placeList);
            }
        });
    }

    @Override
    public void deleteAll() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            int placesCounter = placeDao.getAll().size();
            int deletedPlaces = placeDao.deleteAll();

            Log.i("POSTI CANCELLATI:", String.valueOf(placesCounter));
            if (placesCounter == deletedPlaces) {
                sharedPreferences.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                placeCallback.onSuccessDeletionP();
            }
        });
    }
}
