package com.example.eventiapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.example.eventiapp.model.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM place ORDER BY name ASC")
    List<Place> getAll();

    @Query("SELECT * FROM place WHERE id = :id")
    Place getPlace(String id);

    @Query("SELECT * FROM place WHERE is_favorite = 1 ORDER BY name ASC")
    List<Place> getFavoritePlaces();

    @Query("SELECT COUNT(*) FROM place")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    default List<String> insertPlacesList(List<Place> placeList) {
        return null;
    }

    @Insert
    void insertAll(Place... places);

    @Update
    int updateSingleFavoritePlace(Place place);

    @Update
    int updateListFavoritePlace(List<Place> places);

    @Delete
    void delete(Place place);

    @Delete
    void deleteAllWithoutQuery(Place... places);

    @Query("DELETE FROM place")
    int deleteAll();

    @Query("DELETE FROM place WHERE is_favorite=0")
    void deleteNotFavoritePlaces();
}
