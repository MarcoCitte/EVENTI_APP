package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentHomeBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.EventsRepository;
import com.example.eventiapp.repository.events.EventsResponseCallback;
import com.example.eventiapp.repository.events.IEventsRepository;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding fragmentHomeBinding;

    private List<Events> eventsList;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsViewModel eventsViewModel;
    //private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
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
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String country = "IT"; //POI VERRA PRESA DALLE SHAREDPREFERENCES
        String location="45.5193323, 9.200713"; //BICOCCA
        String date="2023-04-04";
        int limit=50;

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(),
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        //SETTA EVENTO COME PREFERITO
                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);

        String lastUpdate = "0";

        fragmentHomeBinding.progressBar.setVisibility(View.VISIBLE);


        eventsViewModel.getEvents(country, location, date, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
                    Log.i("RESULT", "RESULT");

                    if (result.isSuccess()) {
                        Log.i("SUCCESSO", "SUCCESSO");

                        EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                        List<Events> fetchedEvents = eventsResponse.getEventsList();

                        Log.i("FETCHED EVENTS", fetchedEvents.toString());

                        if (!eventsViewModel.isLoading()) {
                            if (eventsViewModel.isFirstLoading()) {
                                eventsViewModel.setTotalResults(((EventsApiResponse) eventsResponse).getCount());
                                eventsViewModel.setFirstLoading(false);
                                this.eventsList.addAll(fetchedEvents);
                                eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                                        this.eventsList.size());
                            } else {
                                // Updates related to the favorite status of the events
                                eventsList.clear();
                                eventsList.addAll(fetchedEvents);
                                eventsRecyclerViewAdapter.notifyItemChanged(0, fetchedEvents.size());
                            }
                            fragmentHomeBinding.progressBar.setVisibility(View.GONE);
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
                            for (int i = startIndex; i < fetchedEvents.size(); i++) {
                                eventsList.add(fetchedEvents.get(i));
                            }
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, eventsList.size());
                        }
                    } else {
                        Log.i("FALLITO", "FALLITO");

                        ErrorMessageUtil errorMessagesUtil =
                                new ErrorMessageUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                        fragmentHomeBinding.progressBar.setVisibility(View.GONE);
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

                    // Condition to enable the loading of other events while the user is scrolling the list
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
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(eventsList.size(),
                                    eventsList.size() + 1);

                            int page = eventsViewModel.getPage() + 1;
                            eventsViewModel.setPage(page);
                            eventsViewModel.fetchEvents(country,location,date,limit);
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
        fragmentHomeBinding = null;
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}