package com.example.eventiapp.repository.events;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.source.events.BaseEventsLocalDataSource;
import com.example.eventiapp.source.events.BaseEventsRemoteDataSource;
import com.example.eventiapp.source.events.BaseFavoriteEventsDataSource;
import com.example.eventiapp.source.events.BaseMyEventsDataSource;
import com.example.eventiapp.source.places.BaseFavoritePlacesDataSource;
import com.example.eventiapp.source.events.EventsCallback;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.source.places.BaseMyPlacesDataSource;
import com.example.eventiapp.source.places.BasePlacesLocalDataSource;
import com.example.eventiapp.source.places.BasePlacesRemoteDataSource;
import com.example.eventiapp.source.places.PlaceCallback;
import com.example.eventiapp.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositoryWithLiveData implements IRepositoryWithLiveData, EventsCallback, PlaceCallback {

    private static final String TAG = RepositoryWithLiveData.class.getSimpleName();

    private final MutableLiveData<Result> allEventsMutableLiveData;
    private final MutableLiveData<Result> eventsFromSearchLiveData;
    private final MutableLiveData<Result> favoriteEventsMutableLiveData;
    private MutableLiveData<Result> myEventsListLiveData; //EVENTI CREATI DALL'UTENTE CORRENTE
    private MutableLiveData<List<Place>> myPlacesListLiveData; //POSTI CREATI DALL'UTENTE CORRENTE

    private final MutableLiveData<Result> categoryEventsMutableLiveData;
    private final MutableLiveData<Result> eventsInADateMutableLiveData;
    private final MutableLiveData<Result> categoriesEventsMutableLiveData;
    private final MutableLiveData<Result> eventsBetweenDatesMutableLiveData;
    private final MutableLiveData<Result> categoryEventsBetweenDatesMutableLiveData;
    private final MutableLiveData<Result> placeEventsMutableLiveData;
    private final MutableLiveData<Result> singleEventMutableLiveData;
    private final MutableLiveData<List<String>> eventsDateMutableLiveData;
    private final MutableLiveData<String[]> moviesHoursMutableLiveData;
    private final MutableLiveData<List<String>> allCategoriesMutableLiveData;
    private final MutableLiveData<List<String>> categoriesInADateMutableLiveData;
    private final MutableLiveData<String> favoriteCategoryMutableLiveData;
    private final MutableLiveData<Result> favoriteCategoryEventsMutableLiveData;

    //PLACES
    private final MutableLiveData<List<Place>> allPlacesMutableLiveData;
    //private final MutableLiveData<List<Place>> favoritePlacesMutableLiveData;

    private final MutableLiveData<Result> favoritePlacesMutableLiveData2;

    private final MutableLiveData<Place> singlePlaceMutableLiveData;
    private final MutableLiveData<List<Place>> placesFromSearchLiveData;
    private final MutableLiveData<Result> usersCreatedEventsMutableLiveData;

    private final BaseEventsRemoteDataSource eventsRemoteDataSource;
    private final BasePlacesRemoteDataSource placesRemoteDataSource;

    private final BaseEventsLocalDataSource eventsLocalDataSource;
    private final BasePlacesLocalDataSource placesLocalDataSource;

    private final BaseFavoriteEventsDataSource backupDataSource;
    private final BaseFavoritePlacesDataSource backupDataSource2;
    private final BaseMyEventsDataSource backupDataSource3;
    private final BaseMyPlacesDataSource backupDataSource4;


    private final PlaceDetailsSource placeDetailsSource;
    private List<String> dates;
    private int count;

    private LifecycleRegistry lifecycleRegistry;
    private MutableLiveData<List<Place>> usersCreatedPlacesMutableLiveData;


    public RepositoryWithLiveData(BaseEventsRemoteDataSource eventsRemoteDataSource, BasePlacesRemoteDataSource placesRemoteDataSource, BaseEventsLocalDataSource eventsLocalDataSource,
                                  BasePlacesLocalDataSource placesLocalDataSource, PlaceDetailsSource placeDetailsSource, BaseFavoriteEventsDataSource favoriteEventsDataSource, BaseFavoritePlacesDataSource favoritePlacesDataSource, BaseMyEventsDataSource myEventsDataSource, BaseMyPlacesDataSource myPlacesDataSource) {
        allEventsMutableLiveData = new MutableLiveData<>();
        eventsFromSearchLiveData = new MutableLiveData<>();
        favoriteEventsMutableLiveData = new MutableLiveData<>();
        myEventsListLiveData = new MutableLiveData<>();
        myPlacesListLiveData = new MutableLiveData<>();
        categoryEventsMutableLiveData = new MutableLiveData<>();
        eventsInADateMutableLiveData = new MutableLiveData<>();
        eventsBetweenDatesMutableLiveData = new MutableLiveData<>();
        categoriesEventsMutableLiveData = new MutableLiveData<>();
        categoryEventsBetweenDatesMutableLiveData = new MutableLiveData<>();
        singleEventMutableLiveData = new MutableLiveData<>();
        placeEventsMutableLiveData = new MutableLiveData<>();
        eventsDateMutableLiveData = new MutableLiveData<>();
        moviesHoursMutableLiveData = new MutableLiveData<>();
        allPlacesMutableLiveData = new MutableLiveData<>();
        placesFromSearchLiveData = new MutableLiveData<>();
        //favoritePlacesMutableLiveData = new MutableLiveData<>();
        favoritePlacesMutableLiveData2 = new MutableLiveData<>();
        singlePlaceMutableLiveData = new MutableLiveData();
        allCategoriesMutableLiveData = new MutableLiveData<>();
        categoriesInADateMutableLiveData = new MutableLiveData<>();
        favoriteCategoryMutableLiveData = new MutableLiveData<>();
        favoriteCategoryEventsMutableLiveData = new MutableLiveData<>();
        this.eventsRemoteDataSource = eventsRemoteDataSource;
        this.eventsRemoteDataSource.setEventsCallback(this);
        this.placesRemoteDataSource = placesRemoteDataSource;
        this.placesRemoteDataSource.setEventsCallback(this);
        this.eventsLocalDataSource = eventsLocalDataSource;
        this.eventsLocalDataSource.setEventsCallback(this);
        this.placesLocalDataSource = placesLocalDataSource;
        this.placesLocalDataSource.setPlacesCallback((PlaceCallback) this);
        this.placeDetailsSource = placeDetailsSource;
        this.backupDataSource = favoriteEventsDataSource;
        this.backupDataSource.setEventsCallback(this);

        this.backupDataSource2 = favoritePlacesDataSource;
        this.backupDataSource2.setPlacesCallback(this);

        this.backupDataSource3 = myEventsDataSource;
        this.backupDataSource3.setMyEventsCallback(this);

        this.backupDataSource4 = myPlacesDataSource;
        this.backupDataSource4.setMyPlacesCallback(this);

        lifecycleRegistry = new LifecycleRegistry(new LifecycleOwner() {
            @NonNull
            @Override
            public Lifecycle getLifecycle() {
                return lifecycleRegistry;
            }
        });
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        usersCreatedEventsMutableLiveData = new MutableLiveData<>();
        usersCreatedPlacesMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<Result> fetchEvents(String country, String location, String date, String categories, String sort, int limit, long lastUpdate) {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT) {
            eventsRemoteDataSource.getEvents(country, location, date, categories, sort, limit);
        } else {
            eventsLocalDataSource.getEvents();
        }
        return allEventsMutableLiveData;
    }


    @Override
    public void fetchEvents(String country, String location, String date, String categories, String sort, int limit) {
        eventsRemoteDataSource.getEvents(country, location, date, categories, sort, limit);
        //eventsLocalDataSource.getEvents();
    }

    @Override
    public void addEvent(Events events) {
        eventsRemoteDataSource.insertEvents(events);
        eventsLocalDataSource.insertEvents(new ArrayList<>(Collections.singleton(events)));
    }

    @Override
    public void addPlace(Place place) {
        placesRemoteDataSource.insertPlace(place);
        placesLocalDataSource.insertPlaces(new ArrayList<>(Collections.singleton(place)));
    }

    @Override
    public MutableLiveData<Result> getUsersCreatedEvents(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT_USERS_CREATED_EVENTS) {
            Log.e(TAG, "getUsersCreatedEvents: prendo da remoto");
            eventsRemoteDataSource.getUsersCreatedEvents();
        } else {
            eventsLocalDataSource.getUsersCreatedEvents();
        }
        return usersCreatedEventsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getFavoriteEvents(boolean isFirstLoading) {
        if (isFirstLoading) {
            backupDataSource.getFavoriteEvents();
        } else {
            eventsLocalDataSource.getFavoriteEvents();
        }

        return favoriteEventsMutableLiveData;
    }

    @Override
    public MutableLiveData<String> getFavoriteCategory() {
        eventsLocalDataSource.getFavoriteCategory();
        return favoriteCategoryMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getFavoriteCategoryEvents() {
        eventsLocalDataSource.getFavoriteCategoryEvents();
        return favoriteCategoryEventsMutableLiveData;
    }


    public MutableLiveData<Result> getCategoryEvents(String category) {
        eventsLocalDataSource.getCategoryEvents(category);
        return categoryEventsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getPlaceEvents(String id) {
        eventsLocalDataSource.getPlaceEvent(id);
        return placeEventsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getSingleEvent(long id) {
        eventsLocalDataSource.getSingleEvent(id);
        return singleEventMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getEventsInADate(String date) {
        eventsLocalDataSource.getEventsInADate(date);
        return eventsInADateMutableLiveData;
    }

    @Override
    public MutableLiveData<List<String>> getAllCategories() {
        eventsLocalDataSource.getAllCategories();
        return allCategoriesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<String>> getCategoriesInADate(String date) {
        eventsLocalDataSource.getCategoriesInADate(date);
        return categoriesInADateMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getEventsFromSearch(String input) {
        eventsLocalDataSource.getEventsFromSearch(input);
        return eventsFromSearchLiveData;
    }

    @Override
    public MutableLiveData<Result> getCategoriesEvents(List<String> categories) {
        eventsLocalDataSource.getCategoriesEvents(categories);
        return categoriesEventsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getEventsBetweenDates(String firstDate, String endDate) {
        eventsLocalDataSource.getEventsBetweenDates(firstDate, endDate);
        return eventsBetweenDatesMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getCategoryEventsBetweenDates(String firstDate, String endDate, List<String> categories) {
        eventsLocalDataSource.getCategoryEventsBetweenDates(firstDate, endDate, categories);
        return categoryEventsBetweenDatesMutableLiveData;
    }

    @Override
    public MutableLiveData<List<String>> getEventsDates(String name) {
        eventsLocalDataSource.getEventsDates(name);
        return eventsDateMutableLiveData;
    }

    @Override
    public MutableLiveData<String[]> getMoviesHours(String name) {
        eventsLocalDataSource.getMoviesHours(name);
        return moviesHoursMutableLiveData;
    }

    //PLACES --------------------------

    @Override
    public MutableLiveData<List<Place>> fetchPlaces() {
        placesRemoteDataSource.getUsersCreatedPlaces();
        placesLocalDataSource.getPlaces();
        return allPlacesMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getFavoritePlaces(boolean isFirstLoading) {
        if (isFirstLoading) {
            backupDataSource2.getFavoritePlaces();
        } else {
            placesLocalDataSource.getFavoritePlaces();
        }
        return favoritePlacesMutableLiveData2;
    }

    @Override
    public MutableLiveData<Result> getMyEvents(boolean isFirstLoading) {
        if (isFirstLoading) {
            Log.e(TAG, "getMyEvents: isFirstLoading");
            backupDataSource3.getMyEvents();
        } else {
            Log.e(TAG, "getMyEvents: isNotFirstLoading");
            eventsLocalDataSource.getMyEvents();
        }
        return myEventsListLiveData;
    }


    @Override
    public MutableLiveData<Place> getSinglePlace(String id) {
        placesLocalDataSource.getSinglePlace(id);
        return singlePlaceMutableLiveData;
    }

    @Override
    public MutableLiveData<List<Place>> getPlacesFromSearch(String input) {
        placesLocalDataSource.getPlacesFromSearch(input);
        return placesFromSearchLiveData;
    }

    @Override
    public MutableLiveData<Place> getSinglePlaceByName(String name) {
        placesLocalDataSource.getSinglePlaceByName(name);
        return singlePlaceMutableLiveData;
    }

    //----------------------------

    @Override
    public void updateEvents(Events events) {
        eventsLocalDataSource.updateEvents(events);
        if (events.isFavorite()) {
            backupDataSource.addFavoriteEvents(events);
        } else {
            backupDataSource.deleteFavoriteEvents(events);
        }
    }

    @Override
    public int getCount() {
        eventsLocalDataSource.getCount();
        return this.count;
    }

    public void deleteEvents() {
        eventsLocalDataSource.deleteAll();
    }

    @Override
    public void deletePlaces() {
        placesLocalDataSource.deleteAll();
    }

    @Override
    public void deleteFavoriteEvents() {
        eventsLocalDataSource.deleteFavoriteEvents();
    }

    @Override
    public void updatePlace(Place place) {
        placesLocalDataSource.updatePlaces(place);
        if (place.isFavorite()) {
            backupDataSource2.addFavoritePlace(place);
        } else {
            backupDataSource2.deleteFavoritePlace(place);
        }
    }


    @Override
    public void onSuccessFromRemote(EventsApiResponse eventsApiResponse, long lastUpdate) {
        eventsLocalDataSource.insertEvents(eventsApiResponse);
        //PLACES

        /*
        List<Events> fetchedEvents = eventsApiResponse.getEventsList();

        //RIMUOVE EVENTI PRESENTI NELLO STESSO LUOGO COSI DA AVERE EVENTI CHE SI TENGONO IN POSTI DIVERSI PER POTER SALVARE QUEST ULTIMI
        Map<String, Events> map = new HashMap<String, Events>();
        for (Events e : fetchedEvents) {
            if (!e.getPlaces().isEmpty()) {
                String placeName = e.getPlaces().get(0).getName(); //PRENDO IL NOME PERCHè LE API DI PREDICTQ DANNO ID DIVERSI PER LO STESSO POSTO
                if (!map.containsKey(placeName)) {
                    map.put(placeName, e);
                }
            }
        }
        List<Events> eventsNoDuplicates = new ArrayList<>(map.values());
        List<Place> placesAlreadyInDb=new ArrayList<>(allPlacesMutableLiveData.getValue());


        for (Events e : eventsNoDuplicates) {
            boolean isPlaceAlreadyAdded = false;
            for (Place place : placesAlreadyInDb) {
                if (place.getId().equals(e.getPlaces().get(0).getId())) {
                    isPlaceAlreadyAdded = true;
                    break;
                }
            }
            if (!isPlaceAlreadyAdded) {  //COSI RISPARMIO SULLA VELOCITA E SUI SOLDI PER LE API PLACES DI GOOGLE
                //DEVO PRENDERE COORDINATE, INDIRIZZO, FOTO E NUMERO DI TELEFONO DEL POSTO  PIU PRECISI
                Log.i("POSTO NUOVO", e.getPlaces().toString());
               placeDetailsSource.fetchPlaceDetails(e.getPlaces().get(0).getName(), e.getPlaces().get(0).getAddress(), new PlaceDetailsSource.PlaceDetailsListener() {
                    @Override
                    public void onPlaceDetailsFetched(com.google.android.libraries.places.api.model.Place place) {
                        // Hai ottenuto i dettagli del posto
                        Double[] coordinates;
                        if (place.getLatLng() != null) {
                            coordinates = new Double[]{place.getLatLng().latitude, place.getLatLng().longitude};
                        } else {
                            coordinates = new Double[]{e.getCoordinates().get(0), e.getCoordinates().get(1)};
                        }
                        List<Place> placesList = new ArrayList<>();
                        placesList.add(new Place(e.getPlaces().get(0).getId(), StringUtils.capitalizeFirstLetter(place.getName()), e.getPlaces().get(0).getType(), place.getAddress(), place.getId(), Arrays.asList(coordinates), place.getPhoneNumber(), place.getPhotoMetadatas()));
                        placesLocalDataSource.insertPlaces(placesList);
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("ERRORE FETCH PLACE:", message);
                        //PER I POSTI NON TROVATI USO IL COSTUTTORE DI DEFAULT CON LE INFORMAZIONI BASE
                        //placesList.add(new Place(e.getPlaces().get(0).getId(), e.getPlaces().get(0).getName(), e.getPlaces().get(0).getType(), e.getPlaces().get(0).getAddress(), e.getCoordinates()));
                    }
                });


            }else{
                Log.i("POSTO GIA ESISTENTE", e.getPlaces().toString());
            }

        }

         */


    }


    @Override
    public void onSuccessFromRemoteJsoup(EventsApiResponse eventsApiResponse) { // NON USATO
        eventsLocalDataSource.insertEvents(eventsApiResponse);

/*
        //PLACES
        Place uci = new Place("uci_bicocca", "UCI Cinemas Bicocca", "venue", "Via Chiese, 20126 Milan MI, Italy", new double[]{45.5220145, 9.2133497});
        Place pirelli = new Place("pirelli_hangar", "Pirelli HangarBicocca", "venue", "Via Chiese, 2, 20126 Milan MI, Italy", new double[]{45.5203608, 9.2160497});
        Place unimib = new Place("unimib", "Università degli Studi di Milano Bicocca", "venue", "Piazza dell'Ateneo Nuovo, 1, 20126 Milano MI, Italy", new double[]{45.5182898, 9.2111811});
        Place arcimboldi = new Place("arcimboldi", "Teatro arcimboldi", "venue", "Viale dell'Innovazione, 20, 20126 Milano MI, Italy", new double[]{45.514842, 9.2109728});

        List<Place> placeList = new ArrayList<>();
        placeList.add(uci);
        placeList.add(pirelli);
        placeList.add(unimib);
        placeList.add(arcimboldi);
*/

        /*
        allPlacesMutableLiveData.observe(new LifecycleOwner() {
            @NonNull
            @Override
            public Lifecycle getLifecycle() {
                return lifecycleRegistry;
            }
        }, new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                for (Place p : placeList) {
                    boolean isPlaceAlreadyAdded = false;
                    for (Place place : (allPlacesMutableLiveData.getValue())) {
                        if (place.getId().equals(p.getId())) {
                            isPlaceAlreadyAdded = true;
                            break;
                        }
                    }
                    if (!isPlaceAlreadyAdded) {  //COSI RISPARMIO SULLA VELOCITA E SUI SOLDI PER LE API PLACES DI GOOGLE
                        //DEVO PRENDERE COORDINATE, INDIRIZZO, FOTO E NUMERO DI TELEFONO DEL POSTO  PIU PRECISI
                        placeDetailsSource.fetchPlaceDetails(p.getName(), p.getAddress(), new PlaceDetailsSource.PlaceDetailsListener() {
                            @Override
                            public void onPlaceDetailsFetched(com.google.android.libraries.places.api.model.Place place) {
                                List<Place> placesList = new ArrayList<>();
                                placesList.add(new Place(p.getId(), StringUtils.capitalizeFirstLetter(place.getName()), p.getType(), place.getAddress(), place.getId(), p.getCoordinates(), place.getPhoneNumber(), place.getPhotoMetadatas()));
                                placesLocalDataSource.insertPlaces(placesList);
                            }

                            @Override
                            public void onError(String message) {
                                Log.i("ERRORE FETCH PLACE:", message);
                            }
                        });
                    }

                }
            }
        });


         */

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
    public void onEventsPlace(List<Events> events) {
        placeEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onSingleEvent(Events event) {

    }

    @Override
    public void onEventsInADate(List<Events> events) {
        eventsInADateMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onEventsDates(List<String> dates) {
        if (dates.size() > 1) {
            eventsDateMutableLiveData.postValue(dates);
        }
    }

    @Override
    public void onMoviesHours(String[] hours) {
        if (hours.length > 1) {
            moviesHoursMutableLiveData.postValue(hours);
        }
    }

    @Override
    public void onFavoriteCategory(String category) {
        favoriteCategoryMutableLiveData.postValue(category);
    }

    @Override
    public void onFavoriteCategoryEvents(List<Events> events) {
        favoriteCategoryEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
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
            backupDataSource.synchronizeFavoriteEvents(notSynchronizedEventsList);
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

        backupDataSource.deleteAllFavoriteEvents();
    }

    @Override
    public void onCount(int count) {
        this.count = count;
    }

    @Override
    public void onAllCategories(List<String> categories) {
        allCategoriesMutableLiveData.postValue(categories);
    }

    @Override
    public void onCategoriesInADate(List<String> categories) {
        categoriesInADateMutableLiveData.postValue(categories);
    }

    @Override
    public void onCategoriesEvents(List<Events> events) {
        categoriesEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onEventsBetweenDates(List<Events> events) {
        eventsBetweenDatesMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onCategoryEventsBetweenDates(List<Events> events) {
        categoryEventsBetweenDatesMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
    }

    @Override
    public void onEventsFromSearch(List<Events> events) {
        eventsFromSearchLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(events)));
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
        backupDataSource.getFavoriteEvents();
    }

    @Override
    public void onFailureFromCloud(Exception exception) {
        Log.e(TAG, "onFailureFromCloud: " + exception.getMessage());
    }

    @Override
    public void onSuccessSynchronization() {
        Log.d(TAG, "Events synchronized from remote");
    }

    @Override
    public void onSuccessDeletion() {
        Log.d(TAG, "Events delete from DB");
    }

    @Override
    public void onSuccessFromInsertUserCreatedEvent(Events events) {
        /*
        if (events != null && !events.isFavorite()) {
            events.setSynchronized(false);
        }
         */

        //eventsRemoteDataSource.getUsersCreatedEvents();

        Log.d(TAG, "Event inserted in Firebase:" + myEventsListLiveData.getValue());


    }

    @Override
    public void onSuccessFromReadUserCreatedEvent(List<Events> eventsList) {
        //eventsLocalDataSource.insertEvents(eventsList);
        usersCreatedEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(eventsList)));
    }

    @Override
    public void onSuccessFromReadUserCreatedEventLocal(List<Events> eventsList) {
        usersCreatedEventsMutableLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(eventsList)));
    }

    @Override
    public void onSuccessFromRemoteCurrentUserEventsWriting(Events events) {
        if (events != null && !events.isFavorite()) {
            events.setSynchronized(false);
        }

        eventsLocalDataSource.deleteMyEvents(events);
        backupDataSource.getFavoriteEvents();
    }

    @Override
    public void onSuccessFromRemoteCurrentUserEventsReading(List<Events> eventsList) {
        if (eventsList != null) {

            System.out.println("lettura da remoto eventi creati dall'utente corrente:");
            printEventList(eventsList);

            eventsLocalDataSource.insertEvents(eventsList);
            myEventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(eventsList)));
        }
    }

    @Override
    public void onSuccessFromLocalCurrentUserEventsReading(List<Events> eventsList) {
        System.out.println("lettura da locale eventi creati dall'utente corrente:");
        printEventList(eventsList);
        myEventsListLiveData.postValue(new Result.EventsResponseSuccess(new EventsResponse(eventsList)));
    }

    @Override
    public void onSuccessFromLocalCurrentUserEventDeletion(Events events) {
        Log.d(TAG, "onSuccessFromLocalCurrentUserEventDeletion: " + events.getTitle() + "eliminato da locale");
    }

    @Override
    public void onSuccessFromRemoteCurrentUserEventDeletion(Events events) {
        Log.d(TAG, "onSuccessFromRemoteCurrentUserEventDeletion: " + events.getTitle() + "eliminato da remoto");


    }

    public static void printEventList(List<Events> eventList) {
        for (Events event : eventList) {
            Log.d(TAG, "printEventList: " + event.getTitle());
        }
    }
    //PLACECALLBACK -------------------------------------------

    public void onSuccessFromLocalP(List<Place> placeList) {
        if (allPlacesMutableLiveData.getValue() != null) {
            List<Place> placeListOld = allPlacesMutableLiveData.getValue();
            placeListOld.addAll(placeList);
            allPlacesMutableLiveData.postValue(placeListOld);
        } else {
            allPlacesMutableLiveData.postValue(placeList);
        }


    }

    @Override
    public void onFailureFromLocalP(Exception exception) {
        exception.getMessage();
    }


    @Override
    public void onSinglePlace(Place place) {
        singlePlaceMutableLiveData.postValue(place);
    }

    @Override
    public void onPlacesFromSearch(List<Place> places) {
        placesFromSearchLiveData.postValue(places);
    }


    @Override
    public void onPlacesFavoriteStatusChanged(Place place, List<Place> favoritePlaces) {
        List<Place> allPlaces = allPlacesMutableLiveData.getValue();
        if (allPlaces != null) {
            List<Place> oldAllPlaces = allPlaces;
            if (oldAllPlaces.contains(place)) {
                oldAllPlaces.set(oldAllPlaces.indexOf(place), place);
                allPlacesMutableLiveData.postValue(allPlaces);
            }
        }
        favoritePlacesMutableLiveData2.postValue(new Result.PlacesResponseSuccess(favoritePlaces));
    }

    @Override
    public void onPlacesFavoriteStatusChanged(List<Place> placeList) {
        List<Place> notSynchronizedPlacesList = new ArrayList<>();

        for (Place p : placeList) {
            if (!p.isSynchronized()) {
                notSynchronizedPlacesList.add(p);
            }
        }

        if (!notSynchronizedPlacesList.isEmpty()) {
            //BACKUP
        }

        favoritePlacesMutableLiveData2.postValue(new Result.PlacesResponseSuccess(placeList));
    }

    @Override
    public void onDeleteFavoritePlacesSuccess(List<Place> favoritePlaces) {
        List<Place> allPlaces = allPlacesMutableLiveData.getValue();

        if (allPlaces != null) {
            List<Place> oldAllPlaces = allPlaces;
            for (Place p : favoritePlaces) {
                if (oldAllPlaces.contains(p)) {
                    oldAllPlaces.set(oldAllPlaces.indexOf(p), p);
                }
            }
            allPlacesMutableLiveData.postValue(allPlaces);
        }

        if (favoritePlacesMutableLiveData2.getValue() != null) {
            favoritePlaces.clear();
            favoritePlacesMutableLiveData2.postValue(new Result.PlacesResponseSuccess(favoritePlaces));
        }

        //backupDataSource.deleteAllFavoriteNews();
    }

    @Override
    public void onSuccessSynchronizationP() {
        Log.d(TAG, "Places synchronized from remote");
    }

    @Override
    public void onSuccessDeletionP() {

    }

    @Override
    public void onSuccessFromCloudReading2(List<Place> placesList) {
        if (placesList != null) {
            for (Place place : placesList) {
                place.setSynchronized(true);
            }
            placesLocalDataSource.insertPlaces(placesList);
            favoritePlacesMutableLiveData2.postValue(new Result.PlacesResponseSuccess(placesList));
        }
    }

    @Override
    public void onSuccessFromCloudWriting2(Place place) {
        if (place != null && !place.isFavorite()) {
            place.setSynchronized(false);
        }

        placesLocalDataSource.updatePlaces(place);
        backupDataSource2.getFavoritePlaces();
    }

    @Override
    public void onFailureFromCloud2(Exception exception) {

    }

    @Override
    public void onSuccessFromInsertUserCreatedPlace(Place place) {

    }

    @Override
    public void onSuccessFromReadUserCreatedPlaces(List<Place> placesList) {
        //eventsLocalDataSource.insertEvents(eventsList);
        usersCreatedPlacesMutableLiveData.postValue(placesList);
    }

    @Override
    public void onSuccessFromReadUserCreatedPlaceLocal(List<Place> usersCreatedPlaces) {
        usersCreatedPlacesMutableLiveData.postValue(usersCreatedPlaces);

    }

    @Override
    public void onSuccessFromRemoteCurrentUserPlacesReading(List<Place> placesList) {
        if (placesList != null) {

            System.out.println("lettura da remoto eventi creati dall'utente corrente:");
            //printEventList(placesList);

            placesLocalDataSource.insertPlaces(placesList);
            myPlacesListLiveData.postValue(placesList);
        }
    }

    @Override
    public void onSuccessFromLocalCurrentUserPlacesReading(List<Place> myPlaces) {
        myPlacesListLiveData.postValue(myPlaces);

    }

    @Override
    public void onSuccessFromLocalCurrentUserPlaceDeletion(Place place) {

    }

    @Override
    public void deleteMyEvent(Events events) {
        backupDataSource3.deleteMyEvents(events);
        eventsLocalDataSource.deleteMyEvents(events);
    }

    @Override
    public MutableLiveData<List<Place>> getUsersCreatedPlaces(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.FRESH_TIMEOUT_USERS_CREATED_EVENTS) {
            Log.e(TAG, "getUsersCreatedEvents: prendo da remoto");
            placesRemoteDataSource.getUsersCreatedPlaces();
        } else {
            placesLocalDataSource.getUsersCreatedPlaces();
        }
        return usersCreatedPlacesMutableLiveData;    }

    @Override
    public MutableLiveData<List<Place>> getMyPlaces(boolean isFirstLoading) {
        if (isFirstLoading) {
            Log.e(TAG, "getMyPlaces: isFirstLoading");
            backupDataSource4.getMyPlaces();
        } else {
            Log.e(TAG, "getMyPlaces: isNotFirstLoading");
            placesLocalDataSource.getMyPlaces();
        }
        return myPlacesListLiveData;
    }

    @Override
    public void deleteMyPlace(Place place) {
        backupDataSource4.deleteMyPlace(place);
        placesLocalDataSource.deleteMyPlace(place);
    }
    //-----------------------------------------------------------------
}
