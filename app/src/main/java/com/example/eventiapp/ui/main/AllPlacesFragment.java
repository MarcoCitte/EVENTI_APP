package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.PLACES_VIEW_TYPE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentPlacesBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllPlacesFragment extends Fragment {


    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentPlacesBinding fragmentPlacesBinding;

    private List<Events> eventsList;
    private EventsRecyclerViewAdapter placesRecyclerViewAdapter;
    private EventsViewModel eventsViewModel;
    //private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;


    public AllPlacesFragment() {
        // Required empty public constructor
    }

    public static AllPlacesFragment newInstance() {
        return new AllPlacesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IEventsRepositoryWithLiveData eventsRepositoryWithLiveData =
                ServiceLocator.getInstance().getEventsRepository(
                        requireActivity().getApplication()
                );

        if (eventsRepositoryWithLiveData != null) {
            eventsViewModel = new ViewModelProvider(
                    requireActivity(),
                    new EventsViewModelFactory(eventsRepositoryWithLiveData)).get(EventsViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
        }
        eventsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false);
        return fragmentPlacesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String country = "IT"; //POI VERRA PRESA DALLE SHAREDPREFERENCES
        String location = "45.51851, 9.2075123"; //BICOCCA
        double radius = 4.2;
        String sort = "start";
        String date = AllEventsFragment.currentDate();
        int limit = 5000;

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_places);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        placesRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(),PLACES_VIEW_TYPE,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                    }

                    @Override
                    public void onPlacesItemClick(Events events) {
                        //VAI AI DETTAGLI DEL POSTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_placeFragment, bundle);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        //SETTA EVENTO COME PREFERITO
                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(placesRecyclerViewAdapter);

        String lastUpdate = "0";

        fragmentPlacesBinding.progressBar.setVisibility(View.VISIBLE);


        eventsViewModel.getEvents(country, radius + "km@" + location, date, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {

            if (result.isSuccess()) {
                Log.i("SUCCESSO", "SUCCESSO");

                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();

                //RIMUOVE LUOGHI DUPLICATI COSI DA NON AVERE LUOGHI UGUALI
                Map<String, Events> map = new HashMap<String, Events>();
                for (Events e : fetchedEvents) {
                    if(!e.getPlaces().isEmpty() && !e.getCategory().equals("severe-weather") && !e.getCategory().equals("airport-delays")) {
                        String idPlace = e.getPlaces().get(0).getId();
                        if (!map.containsKey(idPlace)) {
                            map.put(idPlace, e);
                        }
                    }
                }
                List<Events> placesList=new ArrayList<>(map.values());


                if (!eventsViewModel.isLoading()) {
                    if (eventsViewModel.isFirstLoading()) {
                        eventsViewModel.setTotalResults(((EventsApiResponse) eventsResponse).getCount());
                        eventsViewModel.setFirstLoading(false);
                        this.eventsList.addAll(placesList);
                        placesRecyclerViewAdapter.notifyItemRangeInserted(0,
                                this.eventsList.size());
                    } else {
                        // Updates related to the favorite status of the events
                        eventsList.clear();
                        eventsList.addAll(placesList);
                        placesRecyclerViewAdapter.notifyItemChanged(0, placesList.size());
                    }
                    fragmentPlacesBinding.progressBar.setVisibility(View.GONE);
                } else {
                    eventsViewModel.setLoading(false);
                    eventsViewModel.setCurrentResults(eventsList.size());

                    int initialSize = eventsList.size();

                    for (int i = 0; i < eventsList.size(); i++) {
                        if (eventsList.get(i) == null) {
                            eventsList.remove(eventsList.get(i));
                        }
                    }
                    int startIndex = (eventsViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                            EVENTS_PAGE_SIZE_VALUE;
                    for (int i = startIndex; i < placesList.size(); i++) {
                        eventsList.add(placesList.get(i));
                    }
                    placesRecyclerViewAdapter.notifyItemRangeInserted(initialSize, eventsList.size());
                }
            } else {
                Log.i("FALLITO", "FALLITO");

                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(view, errorMessagesUtil.
                                getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
                fragmentPlacesBinding.progressBar.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();

                if (isConnected && totalItemCount != eventsViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !eventsViewModel.isLoading()
                            ) &&
                                    eventsViewModel.getEventsResponseLiveData().getValue() != null &&
                                    eventsViewModel.getCurrentResults() != eventsViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> eventsListMutableLiveData = eventsViewModel.getEventsResponseLiveData();

                        if (eventsListMutableLiveData.getValue() != null &&
                                eventsListMutableLiveData.getValue().isSuccess()) {

                            eventsViewModel.setLoading(true);
                            eventsList.add(null);
                            placesRecyclerViewAdapter.notifyItemRangeInserted(eventsList.size(),
                                    eventsList.size() + 1);

                            int page = eventsViewModel.getPage() + 1;
                            eventsViewModel.setPage(page);
                            eventsViewModel.fetchEvents(country, radius + "km@" + location, date, sort, limit);
                        }
                    }
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventsViewModel.setFirstLoading(true);
        eventsViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentPlacesBinding = null;
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
