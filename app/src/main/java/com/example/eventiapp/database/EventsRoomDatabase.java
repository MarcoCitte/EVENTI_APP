package com.example.eventiapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Events.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class EventsRoomDatabase extends RoomDatabase {
    public abstract EventsDao eventsDao();

    private static volatile EventsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EventsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventsRoomDatabase.class, Constants.EVENTS_DATABASE_NAME).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
