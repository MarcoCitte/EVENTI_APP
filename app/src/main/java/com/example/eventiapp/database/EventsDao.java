package com.example.eventiapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.util.DateUtils;

import java.util.List;

@Dao
public interface EventsDao {

    @Query("SELECT DISTINCT * FROM events ORDER BY startDate ASC")
    List<Events> getAll();

    @Query("SELECT DISTINCT * FROM events WHERE startDate >= :date ORDER BY startDate ASC")
    List<Events> getEventsFromADate(String date);

    @Query("SELECT * FROM events WHERE id_db = :id")
    Events getEvents(long id);

    @Query("SELECT * FROM events WHERE is_favorite = 1 ORDER BY startDate DESC")
    List<Events> getFavoriteEvents();

    @Query("SELECT * FROM events WHERE creator_email = :email ORDER BY startDate DESC")
    List<Events> getMyEvents(String email);

    @Query("SELECT * FROM events WHERE creator_email <> null ORDER BY startDate DESC")
    List<Events> getUsersCreatedEvents();

    @Query("SELECT * FROM events WHERE category IN(SELECT category FROM events WHERE is_favorite = 1 GROUP BY category ORDER BY COUNT(category) DESC LIMIT 1) AND is_favorite=0")
    List<Events> getFavoriteCategoryEvents();

    @Query("SELECT category FROM events WHERE is_favorite = 1 GROUP BY category ORDER BY COUNT(category) DESC LIMIT 1")
    String getFavoriteCategory();

    @Query("SELECT DISTINCT * FROM events WHERE category LIKE '%' || :category || '%' ORDER BY startDate ASC")
    List<Events> getCategoryEvents(String category);

    @Query("SELECT DISTINCT * FROM events WHERE places LIKE '%' || :id_place || '%' ORDER BY startDate ASC")
    List<Events> getPlaceEvents(String id_place);

    @Query("SELECT DISTINCT * FROM events WHERE startDate LIKE  '%' || :date || '%' ORDER BY startDate ASC")
    List<Events> getEventsInADate(String date);

    @Query("SELECT DISTINCT COUNT(*) FROM events")
    int count();

    @Query("SELECT DISTINCT category FROM events")
    List<String> getAllCategories();

    @Query("SELECT DISTINCT category FROM events WHERE startDate LIKE  '%' || :date || '%' ")
    List<String> getCategoriesInADate(String date);

    @Query("SELECT DISTINCT * FROM events WHERE category IN (:categories) ORDER BY startDate ASC")
    List<Events> getCategoriesEvents(List<String> categories);

    @Query("SELECT DISTINCT * FROM events WHERE strftime('%Y-%m-%d', date(startDate)) BETWEEN strftime('%Y-%m-%d', date(:startDate)) AND strftime('%Y-%m-%d', date(:endDate)) ORDER BY startDate ASC")
    List<Events> getEventsBetweenDates(String startDate, String endDate);

    @Query("SELECT DISTINCT * FROM events WHERE strftime('%Y-%m-%d', date(startDate)) BETWEEN strftime('%Y-%m-%d', date(:startDate)) AND strftime('%Y-%m-%d', date(:endDate)) AND category IN (:categories) ORDER BY startDate ASC")
    List<Events> getCategoryEventsBetweenDates(String startDate, String endDate, List<String> categories);

    @Query("SELECT DISTINCT startDate FROM events WHERE title = :name ORDER BY startDate ASC")
    List<String> getEventsDates(String name);

    @Query("SELECT DISTINCT hours FROM events WHERE title = :name")
    String[] getMoviesHours(String name);

    //QUERY SEARCH
    @Query("SELECT DISTINCT * FROM events WHERE title LIKE '%' || :input || '%' OR category LIKE '%' || :input || '%' ")
    List<Events> getEventsFromSearch(String input);


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
    @Update
    void editEvent(Events modEvent);
}
