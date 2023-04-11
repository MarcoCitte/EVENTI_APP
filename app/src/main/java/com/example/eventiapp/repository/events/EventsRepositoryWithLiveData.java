package com.example.eventiapp.repository.events;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.source.BaseEventsLocalDataSource;
import com.example.eventiapp.source.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.EventsCallback;
import com.example.eventiapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class EventsRepositoryWithLiveData implements IEventsRepositoryWithLiveData, EventsCallback {

    private static final String TAG = EventsRepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allEventsMutableLiveData;
    private final MutableLiveData<Result> favoriteEventsMutableLiveData;
    private final MutableLiveData<Result> categoryEventsMutableLiveData;
    private final BaseEventsRemoteDataSource eventsRemoteDataSource;
    private BaseEventsLocalDataSource eventsLocalDataSource;


    public EventsRepositoryWithLiveData(BaseEventsRemoteDataSource eventsRemoteDataSource, BaseEventsLocalDataSource eventsLocalDataSource) {
        allEventsMutableLiveData = new MutableLiveData<>();
        favoriteEventsMutableLiveData = new MutableLiveData<>();
        categoryEventsMutableLiveData=new MutableLiveData<>();
        this.eventsRemoteDataSource = eventsRemoteDataSource;
        this.eventsRemoteDataSource.setEventsCallback(this);
        this.eventsLocalDataSource = eventsLocalDataSource;
        this.eventsLocalDataSource.setEventsCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchEvents(String country, String location, String date, String sort, int limit, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT) {
            eventsRemoteDataSource.getEvents(country, location, date, sort, limit);
        } else {
            eventsLocalDataSource.getEvents();
        }
        return allEventsMutableLiveData;
    }

    @Override
    public void fetchEvents(String country, String location, String date, String sort, int limit) {
        eventsRemoteDataSource.getEvents(country, location, date,sort, limit);
    }

    @Override
    public MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading) {
        if (isFirstLoading) {
            //PRENDE I BACKUP
        } else {
            eventsLocalDataSource.getFavoriteEvents();
        }
        return favoriteEventsMutableLiveData;
    }


    public MutableLiveData<Result> getCategoryEvents(String category){
       eventsLocalDataSource.getCategoryEvents(category);
       return categoryEventsMutableLiveData;
    }

    @Override
    public void updateEvents(Events events) {
        eventsLocalDataSource.updateEvents(events);
        if (events.isFavorite()) {
            //AGGIUNGI EVENTO COME PREFERITO
        } else {
            //ELIMINA EVENTO COME PREFERITO
        }
    }

    public void deleteEvents(){
       eventsLocalDataSource.deleteAll();
    }

    @Override
    public void deleteFavoriteEvents() {
        eventsLocalDataSource.deleteFavoriteEvents();
    }

    @Override
    public void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate) {
        //Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(eventsApiResponse);
        //allEventsMutableLiveData.postValue(result);
        eventsLocalDataSource.insertEvents(eventsApiResponse);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(EventsApiResponse eventsApiResponse) {
        if (allEventsMutableLiveData.getValue() != null && allEventsMutableLiveData.getValue().isSuccess()) {
            List<Events> eventsList = ((Result.EventsResponseSuccess) allEventsMutableLiveData.getValue()).getData().getEventsList();
            eventsList.addAll(eventsApiResponse.getEventsList());
            eventsApiResponse.setEventsList(eventsList);
            Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(eventsApiResponse);
            allEventsMutableLiveData.postValue(result);
        } else {
            Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(eventsApiResponse);
            allEventsMutableLiveData.postValue(result);
        }
    }


    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allEventsMutableLiveData.postValue(resultError);
        favoriteEventsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onEventsCategory(List<Events> events) {
        categoryEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onEventsFavoriteStatusChanged(Events events, List<Events> favoriteEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Events> oldAllEvents = ((Result.EventsResponseSuccess) allEventsResult).getData().getEventsList();
            if (oldAllEvents.contains(events)) {
                oldAllEvents.set(oldAllEvents.indexOf(events), events);
                allEventsMutableLiveData.postValue(allEventsResult);
            }
        }
        favoriteEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(favoriteEvents)));
    }

    @Override
    public void onEventsFavoriteStatusChanged(List<Events> events) {
        List<Events> notSynchronizedEventsList = new ArrayList<>();

        for (Events event : events) {
            if (!event.isSynchronized()) {
                notSynchronizedEventsList.add(event);
            }
        }

        if (!notSynchronizedEventsList.isEmpty()) {
            //BACKUP
        }

        favoriteEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onDeleteFavoriteEventsSuccess(List<Events> favoriteEvents) {
        Result allEventsResult = allEventsMutableLiveData.getValue();

        if (allEventsResult != null && allEventsResult.isSuccess()) {
            List<Events> oldAllEvents = ((Result.EventsResponseSuccess) allEventsResult).getData().getEventsList();
            for (Events event : favoriteEvents) {
                if (oldAllEvents.contains(event)) {
                    oldAllEvents.set(oldAllEvents.indexOf(event), event);
                }
            }
            allEventsMutableLiveData.postValue(allEventsResult);
        }

        if (favoriteEventsMutableLiveData.getValue() != null &&
                favoriteEventsMutableLiveData.getValue().isSuccess()) {
            favoriteEvents.clear();
            Result.EventsResponseSuccess result = new Result.EventsResponseSuccess(new EventsResponse(favoriteEvents));
            favoriteEventsMutableLiveData.postValue(result);
        }

        //backupDataSource.deleteAllFavoriteNews();
    }

    @Override
    public void onSuccessFromCloudReading(List<Events> eventsList) {
        if (eventsList != null) {
            for (Events events : eventsList) {
                events.setSynchronized(true);
            }
            eventsLocalDataSource.insertEvents(eventsList);
            favoriteEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(eventsList)));
        }
    }

    @Override
    public void onSuccessFromCloudWriting(Events events) {
        if (events != null && !events.isFavorite()) {
            events.setSynchronized(false);
        }
        eventsLocalDataSource.updateEvents(events);
        //backupDataSource.getFavoriteNews();
    }

    @Override
    public void onFailureFromCloud(Exception exception) {

    }

    @Override
    public void onSuccessSynchronization() {
        Log.d(TAG, "Events synchronized from remote");
    }

    @Override
    public void onSuccessDeletion() {}
}
