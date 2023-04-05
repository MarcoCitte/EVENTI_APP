package com.example.eventiapp.source;


import static com.example.eventiapp.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.eventiapp.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.LAST_UPDATE;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import com.example.eventiapp.database.EventsDao;
import com.example.eventiapp.database.EventsRoomDatabase;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.util.DataEncryptionUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;

import java.util.List;

public class EventsLocalDataSource extends BaseEventsLocalDataSource{

    private final EventsDao eventsDao;
    private final SharedPreferencesUtil sharedPreferences;
    private final DataEncryptionUtil dataEncryptionUtil;

    public EventsLocalDataSource(EventsRoomDatabase eventsRoomDatabase, SharedPreferencesUtil sharedPreferences, DataEncryptionUtil dataEncryptionUtil) {
        this.eventsDao = eventsRoomDatabase.eventsDao();
        this.sharedPreferences = sharedPreferences;
        this.dataEncryptionUtil = dataEncryptionUtil;
    }

    @Override
    public void getEvents() {
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
          EventsApiResponse eventsApiResponse=new EventsApiResponse();
          eventsApiResponse.setEventsList(eventsDao.getAll());
          eventsCallback.onSuccessFromLocal(eventsApiResponse);
        });
    }

    @Override
    public void getFavoriteEvents() {
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
          List<Events> favoriteEvents = eventsDao.getFavoriteEvents();
          eventsCallback.onEventsFavoriteStatusChanged(favoriteEvents);
        });
    }

    @Override
    public void updateEvents(Events events) {
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
          if(events!=null){
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
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
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
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
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
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
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
    public void deleteAll() {
        EventsRoomDatabase.databaseWriteExecutor.execute(() -> {
            int eventsCounter = eventsDao.getAll().size();
            int deletedEvents = eventsDao.deleteAll();

            if (eventsCounter == deletedEvents) {
                sharedPreferences.deleteAll(SHARED_PREFERENCES_FILE_NAME);
                dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                eventsCallback.onSuccessDeletion();
            }
        });
    }
}
