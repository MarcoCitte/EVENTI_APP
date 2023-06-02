package com.example.eventiapp.source.events;


import static com.example.eventiapp.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.eventiapp.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.LAST_UPDATE;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import com.example.eventiapp.database.EventsDao;
import com.example.eventiapp.database.RoomDatabase;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.util.DataEncryptionUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;

import java.util.List;

public class EventsLocalDataSource extends BaseEventsLocalDataSource {

    private final EventsDao eventsDao;
    private final SharedPreferencesUtil sharedPreferences;
    private final DataEncryptionUtil dataEncryptionUtil;

    public EventsLocalDataSource(RoomDatabase roomDatabase, SharedPreferencesUtil sharedPreferences, DataEncryptionUtil dataEncryptionUtil) {
        this.eventsDao = roomDatabase.eventsDao();
        this.sharedPreferences = sharedPreferences;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getEvents() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            EventsApiResponse eventsApiResponse = new EventsApiResponse();
            eventsApiResponse.setEventsList(eventsDao.getAll());
            eventsCallback.onSuccessFromLocal(eventsApiResponse);
        });
    }


    @Override
    public void getFavoriteEvents() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> favoriteEvents = eventsDao.getFavoriteEvents();
            eventsCallback.onEventsFavoriteStatusChanged(favoriteEvents);
        });
    }

    @Override
    public void getFavoriteCategory() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            String category = eventsDao.getFavoriteCategory();
            eventsCallback.onFavoriteCategory(category);
        });
    }

    @Override
    public void getFavoriteCategoryEvents() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getFavoriteCategoryEvents();
            eventsCallback.onFavoriteCategoryEvents(events);
        });
    }

    @Override
    public void getCategoryEvents(String category) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> categoryEvents = eventsDao.getCategoryEvents(category);
            eventsCallback.onEventsCategory(categoryEvents);
        });
    }

    @Override
    public void getSingleEvent(long id) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            Events event = eventsDao.getEvents(id);
            eventsCallback.onSingleEvent(event);
        });
    }

    @Override
    public void getPlaceEvent(String id) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getPlaceEvents(id);
            eventsCallback.onEventsPlace(events);
        });
    }

    @Override
    public void getEventsInADate(String date) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getEventsInADate(date);
            eventsCallback.onEventsInADate(events);
        });
    }

    @Override
    public void getAllCategories() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<String> categories = eventsDao.getAllCategories();
            eventsCallback.onAllCategories(categories);
        });
    }

    @Override
    public void getCategoriesInADate(String date) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<String> categories = eventsDao.getCategoriesInADate(date);
            eventsCallback.onCategoriesInADate(categories);
        });
    }

    @Override
    public void getCategoriesEvents(List<String> categories) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getCategoriesEvents(categories);
            eventsCallback.onCategoriesEvents(events);
        });
    }

    @Override
    public void getEventsBetweenDates(String startDate, String endDate) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getEventsBetweenDates(startDate, endDate);
            eventsCallback.onEventsBetweenDates(events);
        });
    }

    @Override
    public void getCategoryEventsBetweenDates(String startDate, String endDate, List<String> categories) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getCategoryEventsBetweenDates(startDate, endDate, categories);
            eventsCallback.onCategoryEventsBetweenDates(events);
        });
    }

    @Override
    public void getEventsDates(String name) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<String> dates = eventsDao.getEventsDates(name);
            eventsCallback.onEventsDates(dates);
        });
    }

    @Override
    public void getMoviesHours(String name) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            String[] hours = eventsDao.getMoviesHours(name);
            eventsCallback.onMoviesHours(hours);
        });
    }

    @Override
    public void getEventsFromSearch(String input) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> events = eventsDao.getEventsFromSearch(input);
            eventsCallback.onEventsFromSearch(events);
        });
    }

    @Override
    public void updateEvents(Events events) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            if (events != null) {
                int rowUpdatedCounter = eventsDao.updateSingleFavoriteEvents(events);
                if (rowUpdatedCounter == 1) {
                    Events updatedEvents = eventsDao.getEvents(events.getId_db());
                    eventsCallback.onEventsFavoriteStatusChanged(updatedEvents, eventsDao.getFavoriteEvents());
                } else {
                    eventsCallback.onFailureFromLocal(new Exception("ERRORE"));
                }
            } else {
                List<Events> allEvents = eventsDao.getAll();
                for (Events e : allEvents) {
                    e.setSynchronized(false);
                    eventsDao.updateSingleFavoriteEvents(e);
                }
            }
        });
    }

    @Override
    public void deleteFavoriteEvents() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> favoriteEvents = eventsDao.getFavoriteEvents();
            for (Events events : favoriteEvents) {
                events.setFavorite(false);
            }
            int updatedRowsNumber = eventsDao.updateListFavoriteEvents(favoriteEvents);
            if (updatedRowsNumber == favoriteEvents.size()) {
                eventsCallback.onDeleteFavoriteEventsSuccess(favoriteEvents);
            } else {
                eventsCallback.onFailureFromLocal(new Exception("ERRORE"));
            }
        });
    }

    @Override
    public void insertEvents(EventsApiResponse eventsApiResponse) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Events> allEvents = eventsDao.getAll();
            List<Events> eventsList = eventsApiResponse.getEventsList();

            if (eventsList != null) {
                for (Events events : allEvents) {
                    if (eventsList.contains(events)) {
                        eventsList.set(eventsList.indexOf(events), events);
                    }
                }
                List<Long> insertedEventsIds = eventsDao.insertEventsList(eventsList);
                for (int i = 0; i < eventsList.size(); i++) {
                    eventsList.get(i).setId_db(insertedEventsIds.get(i));
                }

                sharedPreferences.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                eventsCallback.onSuccessFromLocal(eventsApiResponse);
            }
        });
    }

    @Override
    public void insertEvents(List<Events> eventsList) {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            if (eventsList != null) {
                List<Events> allEvents = eventsDao.getAll();

                for (Events events : allEvents) {
                    if (eventsList.contains(events)) {
                        events.setSynchronized(true);
                        eventsList.set(eventsList.indexOf(events), events);
                    }
                }

                List<Long> insertedEventsIds = eventsDao.insertEventsList(eventsList);
                for (int i = 0; i < eventsList.size(); i++) {
                    eventsList.get(i).setId_db(insertedEventsIds.get(i));
                }

                EventsApiResponse eventsApiResponse = new EventsApiResponse();
                eventsApiResponse.setEventsList(eventsList);
                eventsCallback.onSuccessSynchronization();
            }
        });
    }

    @Override
    public void getCount() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            int count = eventsDao.count();
            eventsCallback.onCount(count);
        });
    }

    @Override
    public void deleteAll() {
        RoomDatabase.databaseWriteExecutor.execute(() -> {
            int eventsCounter = eventsDao.getAll().size();
            int deletedEvents = eventsDao.deleteAll();

            Log.i("ELEMENTI CANCELLATI:", String.valueOf(eventsCounter));
            if (eventsCounter == deletedEvents) {
                sharedPreferences.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                eventsCallback.onSuccessDeletion();
            }
        });
    }
}
