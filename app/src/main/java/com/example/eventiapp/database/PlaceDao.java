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
    @Query("SELECT DISTINCT  * FROM place ORDER BY name ASC")
    List<Place> getAll();

    @Query("SELECT * FROM place WHERE id = :id")
    Place getPlace(String id);

    @Query("SELECT * FROM place WHERE name = :name")
    Place getPlaceByName(String name);

    @Query("SELECT DISTINCT * FROM place WHERE is_favorite = 1 ORDER BY name ASC")
    List<Place> getFavoritePlaces();

    //QUERY SEARCH
    @Query("SELECT DISTINCT * FROM place WHERE name LIKE '%' || :input || '%' OR address LIKE '%' || :input || '%' ")
    List<Place> getPlacesFromSearch(String input);

    @Query("SELECT DISTINCT COUNT(*) FROM place")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlacesList(List<Place> placeList);

    @Update
    int updateSingleFavoritePlace(Place place);

    @Update
    int updateListFavoritePlace(List<Place> places);

    @Delete
    void delete(Place place);

    @Query("DELETE FROM place")
    int deleteAll();

    @Query("SELECT * FROM place WHERE creator_email <> null")
    List<Place> getUsersCreatedPlaces();

    @Query("SELECT * FROM place WHERE creator_email = :email")
    List<Place> getMyPlaces(String email);
}
