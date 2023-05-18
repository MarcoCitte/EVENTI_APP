package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsAndPlacesPagerAdapter;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.adapter.PlacesRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentContainerEventsPlacesCalendarBinding;
import com.example.eventiapp.databinding.FragmentSearchBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;

    private List<Events> eventsList;
    private List<Place> placeList;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;


    public SearchFragment() {
    }

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    EventsAndPlacesPagerAdapter eventsAndPlacesPagerAdapter;
    private LinearLayoutManager layoutManagerEvents;
    private LinearLayoutManager layoutManagerPlaces;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        placeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return fragmentSearchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = null;
        String isPlace = null;
        if (getArguments() != null) {
            String sort = getArguments().getString("sort");
            bundle = new Bundle();
            bundle.putString("sort", sort);
            isPlace = getArguments().getString("place");
        }

        tabLayout = fragmentSearchBinding.tabLayout;
        viewPager2 = fragmentSearchBinding.viewPager;
        eventsAndPlacesPagerAdapter = new EventsAndPlacesPagerAdapter(this);
        eventsAndPlacesPagerAdapter.setBundle(bundle);
        viewPager2.setAdapter(eventsAndPlacesPagerAdapter);

        if (isPlace != null) {  //HA SCELTO SEE ALL VENUES
            viewPager2.setCurrentItem(1); //IMPOSTA TAB ALL PLACES
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        //SEARCH


        layoutManagerEvents =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        layoutManagerPlaces =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        //EVENTS

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(), 0,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_eventFragment, bundle);
                    }

                    @Override
                    public void onExportButtonPressed(Events events) {
                        ContentValues event = new ContentValues();
                        event.put(CalendarContract.Events.CALENDAR_ID, events.getId_db());
                        event.put(CalendarContract.Events.TITLE, events.getTitle());
                        if (events.getPlaces().get(0).getAddress() != null) {
                            event.put(CalendarContract.Events.EVENT_LOCATION, events.getPlaces().get(0).getAddress());
                        }
                        event.put(CalendarContract.Events.DESCRIPTION, events.getDescription());
                        if (events.getStart() != null) {
                            event.put(CalendarContract.Events.DTSTART, events.getStart());
                        }
                        if (events.getEnd() != null) {
                            event.put(CalendarContract.Events.DTEND, events.getEnd());
                        }
                        event.put(CalendarContract.Events.EVENT_TIMEZONE, events.getTimezone());

                        // Inserisci l'evento nel calendario
                        Uri uri = requireContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);
                    }

                    @Override
                    public void onShareButtonPressed(Events events) {
                        ShareUtils.shareEvent(requireContext(),events);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        //SETTA EVENTO COME PREFERITO
                    }
                });
        fragmentSearchBinding.recyclerViewEvents.setLayoutManager(layoutManagerEvents);
        fragmentSearchBinding.recyclerViewEvents.setAdapter(eventsRecyclerViewAdapter);


        //PLACES

        placesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(placeList,
                requireActivity().getApplication(), 2, new PlacesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onPlacesItemClick(Place place) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("place", place);
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_placeFragment, bundle);
            }

            @Override
            public void onShareButtonPressed(Place place) {
                ShareUtils.sharePlace(requireContext(),place);

            }

            @Override
            public void onFavoriteButtonPressed(int position) {

            }
        });
        fragmentSearchBinding.recyclerViewPlaces.setLayoutManager(layoutManagerPlaces);
        fragmentSearchBinding.recyclerViewPlaces.setAdapter(placesRecyclerViewAdapter);


        fragmentSearchBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.equals("")) {
                    fragmentSearchBinding.tabLayout.setVisibility(View.GONE);
                    fragmentSearchBinding.viewPager.setVisibility(View.GONE);
                    fragmentSearchBinding.scrollView.setVisibility(View.VISIBLE);

                    eventsAndPlacesViewModel.getEventsFromSearchLiveData(query).observe(getViewLifecycleOwner(), result -> {
                        showEvents(result);
                    });

                    eventsAndPlacesViewModel.getPlacesFromSearchLiveData(query).observe(getViewLifecycleOwner(), result -> {
                        showPlaces(result);
                    });
                }else{
                    fragmentSearchBinding.scrollView.setVisibility(View.GONE);
                    fragmentSearchBinding.recyclerViewEvents.setVisibility(View.GONE);
                    fragmentSearchBinding.textViewEvents.setVisibility(View.GONE);
                    fragmentSearchBinding.recyclerViewPlaces.setVisibility(View.GONE);
                    fragmentSearchBinding.textViewPlaces.setVisibility(View.GONE);
                    fragmentSearchBinding.tabLayout.setVisibility(View.VISIBLE);
                    fragmentSearchBinding.viewPager.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")) {
                    fragmentSearchBinding.tabLayout.setVisibility(View.GONE);
                    fragmentSearchBinding.viewPager.setVisibility(View.GONE);
                    fragmentSearchBinding.scrollView.setVisibility(View.VISIBLE);

                    eventsAndPlacesViewModel.getEventsFromSearchLiveData(newText).observe(getViewLifecycleOwner(), result -> {
                        showEvents(result);
                    });
                    eventsAndPlacesViewModel.getPlacesFromSearchLiveData(newText).observe(getViewLifecycleOwner(), result -> {
                        showPlaces(result);
                    });
                }else{
                    fragmentSearchBinding.scrollView.setVisibility(View.GONE);
                    fragmentSearchBinding.recyclerViewEvents.setVisibility(View.GONE);
                    fragmentSearchBinding.textViewEvents.setVisibility(View.GONE);
                    fragmentSearchBinding.recyclerViewPlaces.setVisibility(View.GONE);
                    fragmentSearchBinding.textViewPlaces.setVisibility(View.GONE);
                    fragmentSearchBinding.tabLayout.setVisibility(View.VISIBLE);
                    fragmentSearchBinding.viewPager.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

    }


    private void showEvents(Result result) {
        if (result.isSuccess()) {

            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
            List<Events> fetchedEvents = eventsResponse.getEventsList();

            if (!eventsAndPlacesViewModel.isLoading()) {
                eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                this.eventsList.clear();
                this.eventsList.addAll(fetchedEvents);
                eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                        this.eventsList.size());
                fragmentSearchBinding.progressBarEvents.setVisibility(View.GONE);
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
            if (eventsList.size() > 0) {
                fragmentSearchBinding.textViewEvents.setVisibility(View.VISIBLE);
                fragmentSearchBinding.recyclerViewEvents.setVisibility(View.VISIBLE);
            }else{
                fragmentSearchBinding.recyclerViewEvents.setVisibility(View.GONE);
                fragmentSearchBinding.textViewEvents.setVisibility(View.GONE);
            }
            //fragmentSearchBinding.numberOfEvents.setText(String.valueOf(eventsList.size()));

        } else {
            Log.i("FALLITO", "FALLITO ALL EVENTS");

            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage(((Result.Error) result).getMessage()),
                    Snackbar.LENGTH_SHORT).show();
            fragmentSearchBinding.progressBarEvents.setVisibility(View.GONE);
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
                    this.placeList.addAll(fetchedPlaces);
                    placesRecyclerViewAdapter.notifyItemRangeInserted(0,
                            this.placeList.size());
                } else {
                    // Updates related to the favorite status of the places
                    placesRecyclerViewAdapter.notifyItemRangeRemoved(0, this.placeList.size());
                    this.placeList.clear();
                    this.placeList.addAll(fetchedPlaces);
                    placesRecyclerViewAdapter.notifyItemChanged(0, fetchedPlaces.size());
                }
                fragmentSearchBinding.progressBarPlaces.setVisibility(View.GONE);
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

            if (placesList.size() > 0) {
                fragmentSearchBinding.textViewPlaces.setVisibility(View.VISIBLE);
                fragmentSearchBinding.recyclerViewPlaces.setVisibility(View.VISIBLE);
            }else{
                fragmentSearchBinding.textViewPlaces.setVisibility(View.GONE);
                fragmentSearchBinding.recyclerViewPlaces.setVisibility(View.GONE);
            }
        } else {
            Log.i("FALLITO", "FALLITO");

            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage("ERRORE"),
                    Snackbar.LENGTH_SHORT).show();
            fragmentSearchBinding.progressBarPlaces.setVisibility(View.GONE);
        }


    }
}