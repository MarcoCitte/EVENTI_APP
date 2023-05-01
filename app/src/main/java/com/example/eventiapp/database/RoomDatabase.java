package com.example.eventiapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Events.class, Place.class}, version = 27)
@TypeConverters({Converters.class})
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract EventsDao eventsDao();
    public abstract PlaceDao placeDao();

    private static volatile RoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDatabase.class, Constants.EVENTS_DATABASE_NAME).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
