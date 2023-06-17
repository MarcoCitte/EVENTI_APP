package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.LAST_UPDATE;
import static com.example.eventiapp.util.Constants.REQUEST_CODE;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_LANGUAGE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.adapter.PlacesRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentHomeBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.LanguageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding fragmentHomeBinding;

    private List<Events> eventsList;
    private List<Events> eventsListOrderByRank;
    private List<Events> eventsListForYou;
    private List<Place> placesList;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapterRank;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapterChosenForYou;
    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;

    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    private RecyclerView recyclerViewUE;
    private RecyclerView recyclerViewMA;
    private RecyclerView recyclerViewCFY;
    private RecyclerView recyclerViewEV;

    private LinearLayoutManager layoutManagerUE;
    private LinearLayoutManager layoutManagerMA;
    private LinearLayoutManager layoutManagerCFY;
    private LinearLayoutManager layoutManagerEV;


    private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

    //CAMPI QUERY
    String country = "IT"; //POI VERRA PRESA DALLE SHAREDPREFERENCES
    String location = "45.51851,9.2075123"; //BICOCCA
    double radius = 4.2;
    String sort = "start";
    String date = DateUtils.currentDate();
    String categories = "conferences,expos,concerts,festivals,performing-arts,sports,community";
    int limit = 5000;
    String lastUpdate = "0";


    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        String language = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_LANGUAGE);
        //LanguageUtil.setAppLanguage(requireContext(), language);

        IRepositoryWithLiveData eventsRepositoryWithLiveData =
                ServiceLocator.getInstance().getRepository(
                        requireActivity().getApplication()
                );

        if (eventsRepositoryWithLiveData != null) {
            eventsAndPlacesViewModel = new ViewModelProvider(
                    requireActivity(),
                    new EventsAndPlacesViewModelFactory(eventsRepositoryWithLiveData)).get(EventsAndPlacesViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
        }
        eventsList = new ArrayList<>();
        eventsListForYou = new ArrayList<>();
        eventsListOrderByRank = new ArrayList<>();
        placesList = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsAndPlacesViewModel.getUserCreatedEvents(0).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();
                printEventList(fetchedEvents);
            }
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        recyclerViewUE = fragmentHomeBinding.recyclerViewUE;
        recyclerViewMA = fragmentHomeBinding.recyclerViewMA;
        recyclerViewCFY = fragmentHomeBinding.recyclerViewCFY;
        recyclerViewEV = fragmentHomeBinding.recyclerViewEV;

        layoutManagerUE =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        layoutManagerMA =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        layoutManagerCFY =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        layoutManagerEV =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(), 3,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventFragment, bundle);
                    }

                    @Override
                    public void onExportButtonPressed(Events events) {
                        ShareUtils.addToCalendar(requireContext(), events);
                    }

                    @Override
                    public void onShareButtonPressed(Events events) {
                        ShareUtils.shareEvent(requireContext(), events);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        eventsList.get(position).setFavorite(!eventsList.get(position).isFavorite());
                        eventsAndPlacesViewModel.updateEvents(eventsList.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {

                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {

                    }
                });

        eventsRecyclerViewAdapterRank = new EventsRecyclerViewAdapter(eventsListOrderByRank,
                requireActivity().getApplication(), 3,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventFragment, bundle);
                    }

                    @Override
                    public void onExportButtonPressed(Events events) {
                        ShareUtils.addToCalendar(requireContext(), events);
                    }

                    @Override
                    public void onShareButtonPressed(Events events) {
                        ShareUtils.shareEvent(requireContext(), events);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        eventsListOrderByRank.get(position).setFavorite(!eventsListOrderByRank.get(position).isFavorite());
                        eventsAndPlacesViewModel.updateEvents(eventsListOrderByRank.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {

                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {

                    }
                });

        eventsRecyclerViewAdapterChosenForYou = new EventsRecyclerViewAdapter(eventsListForYou,
                requireActivity().getApplication(), 3,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_eventFragment, bundle);
                    }

                    @Override
                    public void onExportButtonPressed(Events events) {
                        ShareUtils.addToCalendar(requireContext(), events);
                    }

                    @Override
                    public void onShareButtonPressed(Events events) {
                        ShareUtils.shareEvent(requireContext(), events);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        eventsListForYou.get(position).setFavorite(!eventsListForYou.get(position).isFavorite());
                        eventsAndPlacesViewModel.updateEvents(eventsListForYou.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {

                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {

                    }
                });

        placesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(placesList,
                requireActivity().getApplication(), 4,
                new PlacesRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onPlacesItemClick(Place place) {
                        //VAI AI DETTAGLI DEL POSTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("place", place);
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_placeFragment, bundle);
                    }

                    @Override
                    public void onShareButtonPressed(Place place) {
                        ShareUtils.sharePlace(requireContext(), place);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        placesList.get(position).setFavorite(!placesList.get(position).isFavorite());
                        eventsAndPlacesViewModel.updatePlace(placesList.get(position));
                    }

                    @Override
                    public void onModePlaceButtonPressed(Place place) {

                    }

                    @Override
                    public void onDeletePlaceButtonPressed(Place place) {

                    }
                });


        recyclerViewUE.setLayoutManager(layoutManagerUE);
        recyclerViewUE.setAdapter(eventsRecyclerViewAdapter);

        recyclerViewMA.setLayoutManager(layoutManagerMA);

        recyclerViewCFY.setLayoutManager(layoutManagerCFY);
        recyclerViewCFY.setAdapter(eventsRecyclerViewAdapterChosenForYou);

        recyclerViewEV.setLayoutManager(layoutManagerEV);
        recyclerViewEV.setAdapter(placesRecyclerViewAdapter);


        fragmentHomeBinding.progressBarUE.setVisibility(View.VISIBLE);
        fragmentHomeBinding.progressBarMA.setVisibility(View.VISIBLE);
        fragmentHomeBinding.progressBarCFY.setVisibility(View.VISIBLE);
        fragmentHomeBinding.progressBarEV.setVisibility(View.VISIBLE);

        if (sharedPreferencesUtil.readStringData(
                SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(
                    SHARED_PREFERENCES_FILE_NAME, LAST_UPDATE);
        }

        eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categories, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            showEvents(result); //UPCOMING EVENTS E ORDER BY RANK
        });


        eventsAndPlacesViewModel.getFavoriteCategoryEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            showCategoryEvents(result);  //SCELTI PER L'UTENTE IN BASE ALLE SUE PREFERENZE
        });

        eventsAndPlacesViewModel.getPlaces().observe(getViewLifecycleOwner(), result -> {
            showPlaces(result); //POSTI
        });

        //SEE ALL EVENTS

        fragmentHomeBinding.textViewAllEventsUE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sort", "Earliest date");
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_containerEventsPlacesCalendar, bundle);
            }
        });

        fragmentHomeBinding.textViewAllEventsMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sort", "Rank");
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_containerEventsPlacesCalendar, bundle);
            }
        });

        fragmentHomeBinding.textViewAllPlacesEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("place", "place");
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_containerEventsPlacesCalendar, bundle);
            }
        });


    }


    public void sortEvents(String sortingParameter, List<Events> eventsList) {
        switch (sortingParameter) {
            case "Earliest date":
            case "Più recente":
                Collections.sort(eventsList, new Events.SortByMostRecent());
                break;
            case "Rank":
                Collections.sort(eventsList, new Events.SortByRank());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventsAndPlacesViewModel.setFirstLoading(true);
        eventsAndPlacesViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentHomeBinding = null;
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showEvents(Result result) {
        if (result.isSuccess()) {
            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
            List<Events> fetchedEvents = eventsResponse.getEventsList();
            if (!eventsAndPlacesViewModel.isLoading()) {
                if (eventsAndPlacesViewModel.isFirstLoading()) {
                    eventsAndPlacesViewModel.setTotalResults(((EventsApiResponse) eventsResponse).getCount());
                    eventsAndPlacesViewModel.setFirstLoading(false);
                    this.eventsList.addAll(fetchedEvents);
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                            this.eventsList.size());
                } else {
                    eventsListOrderByRank.clear();
                    eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, eventsList.size());
                    eventsList.clear();
                    eventsList.addAll(fetchedEvents);
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(0, fetchedEvents.size());
                }
                fragmentHomeBinding.progressBarUE.setVisibility(View.GONE);
                fragmentHomeBinding.progressBarMA.setVisibility(View.GONE);
            } else {
                eventsAndPlacesViewModel.setLoading(false);
                eventsAndPlacesViewModel.setCurrentResults(eventsList.size());

                int initialSize = eventsList.size();

                for (int i = 0; i < eventsList.size(); i++) {
                    if (eventsList.get(i) == null) {
                        eventsList.remove(eventsList.get(i));
                    }
                }
                int startIndex = (eventsAndPlacesViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                        EVENTS_PAGE_SIZE_VALUE;
                for (int i = startIndex; i < fetchedEvents.size(); i++) {
                    eventsList.add(fetchedEvents.get(i));
                }
                eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, eventsList.size());
            }

            //RIORDINO COL SORT LA LISTA DEGLI EVENTI SENZA FARE PIU QUERY
            this.eventsListOrderByRank.addAll(eventsList);
            sortEvents("Rank", eventsListOrderByRank);
            recyclerViewMA.setAdapter(eventsRecyclerViewAdapterRank);

        } else {
            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage(((Result.Error) result).getMessage()),
                    Snackbar.LENGTH_SHORT).show();
            fragmentHomeBinding.progressBarUE.setVisibility(View.GONE);
            fragmentHomeBinding.progressBarMA.setVisibility(View.GONE);

        }
    }

    private void showCategoryEvents(Result result) {
        if (result.isSuccess()) {
            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
            List<Events> fetchedEvents = eventsResponse.getEventsList();
            if (!fetchedEvents.isEmpty()) {
                fragmentHomeBinding.textViewAddFavoriteEvents.setVisibility(View.GONE);
                fragmentHomeBinding.textViewAllEventsCFY.setVisibility(View.VISIBLE);

                if (!eventsAndPlacesViewModel.isLoading()) {
                    if (eventsAndPlacesViewModel.isFirstLoading()) {
                        // eventsAndPlacesViewModel.setTotalResults(((EventsApiResponse) eventsResponse).getCount());
                        eventsAndPlacesViewModel.setFirstLoading(false);
                        eventsRecyclerViewAdapterChosenForYou.notifyItemRangeRemoved(0, eventsListForYou.size());
                        this.eventsListForYou.addAll(fetchedEvents);
                        eventsRecyclerViewAdapterChosenForYou.notifyItemRangeInserted(0,
                                this.eventsListForYou.size());
                    } else {
                        // Updates related to the favorite status of the events
                        eventsRecyclerViewAdapterChosenForYou.notifyItemRangeRemoved(0, eventsListForYou.size());
                        eventsListForYou.clear();
                        eventsListForYou.addAll(fetchedEvents);
                        eventsRecyclerViewAdapterChosenForYou.notifyItemRangeInserted(0, fetchedEvents.size());
                    }
                    fragmentHomeBinding.progressBarCFY.setVisibility(View.GONE);
                } else {
                    eventsAndPlacesViewModel.setLoading(false);
                    eventsAndPlacesViewModel.setCurrentResults(eventsListForYou.size());
                    int initialSize = eventsListForYou.size();

                    for (int i = 0; i < eventsListForYou.size(); i++) {
                        if (eventsListForYou.get(i) == null) {
                            eventsListForYou.remove(eventsListForYou.get(i));
                        }
                    }
                    int startIndex = (eventsAndPlacesViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                            EVENTS_PAGE_SIZE_VALUE;
                    for (int i = startIndex; i < fetchedEvents.size(); i++) {
                        eventsListForYou.add(fetchedEvents.get(i));
                    }
                    eventsRecyclerViewAdapterChosenForYou.notifyItemRangeInserted(initialSize, eventsListForYou.size());
                }
            } else {
                eventsRecyclerViewAdapterChosenForYou.notifyItemRangeRemoved(0, eventsListForYou.size());
                eventsListForYou.clear();
                fragmentHomeBinding.textViewAddFavoriteEvents.setVisibility(View.VISIBLE);
                fragmentHomeBinding.textViewAllEventsCFY.setVisibility(View.GONE);
                fragmentHomeBinding.progressBarCFY.setVisibility(View.GONE);
            }
        } else {
            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage(((Result.Error) result).getMessage()),
                    Snackbar.LENGTH_SHORT).show();
            fragmentHomeBinding.progressBarCFY.setVisibility(View.GONE);
        }
    }


    private void showPlaces(List<Place> placesList) {
        if (placesList != null) {
            Log.i("SUCCESSO", "SUCCESSO");

            List<Place> fetchedPlaces = new ArrayList<>(placesList);

            if (!eventsAndPlacesViewModel.isLoading()) {
                if (eventsAndPlacesViewModel.isFirstLoading()) {
                    eventsAndPlacesViewModel.setTotalResults(fetchedPlaces.size());
                    eventsAndPlacesViewModel.setFirstLoading(false);
                    this.placesList.addAll(fetchedPlaces);
                    placesRecyclerViewAdapter.notifyItemRangeInserted(0,
                            this.placesList.size());
                } else {
                    // Updates related to the favorite status of the places
                    placesRecyclerViewAdapter.notifyItemRangeRemoved(0, this.placesList.size());
                    this.placesList.clear();
                    this.placesList.addAll(fetchedPlaces);
                    placesRecyclerViewAdapter.notifyItemChanged(0, fetchedPlaces.size());
                }
                fragmentHomeBinding.progressBarEV.setVisibility(View.GONE);
            } else {
                eventsAndPlacesViewModel.setLoading(false);
                eventsAndPlacesViewModel.setCurrentResults(placesList.size());

                int initialSize = placesList.size();

                for (int i = 0; i < placesList.size(); i++) {
                    if (placesList.get(i) == null) {
                        placesList.remove(placesList.get(i));
                    }
                }
                int startIndex = (eventsAndPlacesViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                        EVENTS_PAGE_SIZE_VALUE;
                for (int i = startIndex; i < fetchedPlaces.size(); i++) {
                    placesList.add(fetchedPlaces.get(i));
                }
                placesRecyclerViewAdapter.notifyItemRangeInserted(initialSize, placesList.size());
            }
        } else {
            Log.i("FALLITO", "FALLITO");

            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage("ERRORE"),
                    Snackbar.LENGTH_SHORT).show();
            fragmentHomeBinding.progressBarEV.setVisibility(View.GONE);
        }


    }

    public static void printEventList(List<Events> eventList) {
        for (Events event : eventList) {
            Log.d(TAG, "printEventList: " + event.getTitle());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categories, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            showEvents(result); //UPCOMING EVENTS E ORDER BY RANK
        });
        eventsAndPlacesViewModel.getFavoriteCategoryEventsLiveData().observe(getViewLifecycleOwner(), result -> {
            showCategoryEvents(result);  //SCELTI PER L'UTENTE IN BASE ALLE SUE PREFERENZE
        });
        eventsAndPlacesViewModel.getPlaces().observe(getViewLifecycleOwner(), result -> {
            showPlaces(result); //POSTI
        });
    }
}