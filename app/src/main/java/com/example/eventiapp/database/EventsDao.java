package com.example.eventiapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventiapp.model.Events;

import java.util.List;

@Dao
public interface EventsDao {
    @Query("SELECT * FROM events ORDER BY startDate ASC")
    List<Events> getAll();

    @Query("SELECT * FROM events WHERE id_db = :id")
    Events getEvents(long id);

    @Query("SELECT * FROM events WHERE is_favorite = 1 ORDER BY startDate ASC")
    List<Events> getFavoriteEvents();

    @Query("SELECT * FROM events WHERE category = :category ORDER BY startDate ASC")
    List<Events> getCategoryEvents(String category);

    @Query("SELECT * FROM events WHERE places LIKE '%' || :id_place || '%' ORDER BY startDate ASC")
    List<Events> getPlaceEvents(String id_place);

    @Query("SELECT COUNT(*) FROM events")
    int count();

    @Query("SELECT startDate FROM events WHERE title = :name")
    List<String> getEventsDates(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertEventsList(List<Events> eventsList);

    @Insert
    void insertAll(Events... events);

    @Update
    int updateSingleFavoriteEvents(Events events);

    @Update
    int updateListFavoriteEvents(List<Events> events);

    @Delete
    void delete(Events events);

    @Delete
    void deleteAllWithoutQuery(Events... events);

    @Query("DELETE FROM events")
    int deleteAll();

    @Query("DELETE FROM events WHERE is_favorite=0")
    void deleteNotFavoriteEvents();
}
