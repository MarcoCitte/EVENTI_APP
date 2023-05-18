package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.REQUEST_CODE;

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
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AllEventsFragment extends Fragment implements MyDialogEventsFragment.MyDialogListener {

    private static final String TAG = AllEventsFragment.class.getSimpleName();

    private FragmentAllEventsBinding fragmentAllEventsBinding;

    private List<Events> eventsList;
    private List<String> allCategories;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private String[] listItemsSort;

    //private SharedPreferencesUtil sharedPreferencesUtil;

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
    List<String> checkedCategories;
    String firstDate, endDate;


    public AllEventsFragment() {
        // Required empty public constructor
    }

    public static AllEventsFragment newInstance(Bundle bundle) {
        AllEventsFragment fragment = new AllEventsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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
        allCategories = new ArrayList<>();
        listItemsSort = requireContext().getResources().getStringArray(R.array.sorting_parameters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAllEventsBinding = FragmentAllEventsBinding.inflate(inflater, container, false);
        return fragmentAllEventsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && (!Objects.equals(getArguments().getString("sort"), null))) {
            String sort = getArguments().getString("sort");
            sortingParameter = sort;
            for (int i = 0; i < listItemsSort.length; i++) {
                if (sortingParameter.equals(listItemsSort[i])) {
                    lastSelectedSortingParameter = i;
                }
            }
        } else {
            sortingParameter = "Earliest date";
            lastSelectedSortingParameter = 0;
        }

        //PERMESSI CALENDARIO
        // Verifica se l'app ha i permessi di lettura del calendario
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Se non ha i permessi, li richiede all'utente
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CODE);
        }

// Verifica se l'app ha i permessi di scrittura del calendario
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Se non ha i permessi, li richiede all'utente
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE);
        }

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

        recyclerView = view.findViewById(R.id.recyclerview_events);
        layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(), 0,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_containerEventsPlacesCalendar_to_eventFragment, bundle);
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

                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        eventsList.get(position).setFavorite(!eventsList.get(position).isFavorite());
                        eventsAndPlacesViewModel.updateEvents(eventsList.get(position));                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);

        String lastUpdate = "0";

        fragmentAllEventsBinding.progressBar.setVisibility(View.VISIBLE);

        //eventsAndPlacesViewModel.deleteEvents(); //IN QUESTO MODO MI CARICA SEMPRE EVENTI NUOVI A PARTIRE DAL GIORNO CORRENTE

        eventsAndPlacesViewModel.getAllCategories().observe(getViewLifecycleOwner(), result -> {
            if (!result.isEmpty()) {
                allCategories = result;
            }
        });

        eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categories, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            showEvents(result, 0);
        });

        //FILTRI

        fragmentAllEventsBinding.filtersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(allCategories);
            }
        });

        fragmentAllEventsBinding.sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSorting();
            }
        });

    }


    public void showDialog(List<String> allCategories) {
        MyDialogEventsFragment dialogFragment = new MyDialogEventsFragment(allCategories);
        Bundle bundle = new Bundle();
        if (checkedCategories != null && !checkedCategories.isEmpty()) {
            bundle.putStringArrayList("categories", (ArrayList<String>) checkedCategories);
            dialogFragment.setArguments(bundle);
        }
        if (firstDate != null && endDate != null) {
            bundle.putString("fromDate", firstDate);
            bundle.putString("toDate", endDate);
            dialogFragment.setArguments(bundle);
        }
        dialogFragment.show(getChildFragmentManager(), "MyDialogFragment");
    }

    public void showSorting() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle("ORDER BY")
                .setSingleChoiceItems(listItemsSort, lastSelectedSortingParameter, (dialog, i) -> {
                    sortingParameter = listItemsSort[i];
                    lastSelectedSortingParameter = i;
                    if (!eventsList.isEmpty()) {
                        sortEvents(sortingParameter, eventsList);
                    }
                }).setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {
                }).show();
    }

    public void sortEvents(String sortingParameter, List<Events> eventsList) {
        switch (sortingParameter) {
            case "Earliest date":
            case "Più recente":
                Collections.sort(eventsList, new Events.SortByMostRecent());
                break;
            case "Latest recent":
            case "Meno recente":
                Collections.sort(eventsList, new Events.SortByLeastRecent());
                break;
            case "Rank":
            case "Più attesi":
                Collections.sort(eventsList, new Events.SortByRank());
                break;
            case "Alphabet (A-Z)":
            case "Alfabetico (A-Z)":
                Collections.sort(eventsList, new Events.SortByAlphabetAZ());
                break;
            case "Alphabet (Z-A)":
            case "Alfabetico (Z-A)":
                Collections.sort(eventsList, new Events.SortByAlphabetZA());
                break;
        }
        eventsRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFilterApply(List<String> categories, String fromDate, String toDate) {
        Log.i(TAG, "FILTRI");
        firstDate = fromDate;
        endDate = toDate;
        checkedCategories = categories;
        if (categories.isEmpty() && Objects.equals(fromDate, "") && Objects.equals(toDate, "")) {
            //NON FARE NIENTE
        } else if (categories.isEmpty() && !Objects.equals(fromDate, "") && !Objects.equals(toDate, "")) {
            eventsAndPlacesViewModel.getEventsBetweenDatesLiveData(fromDate, toDate).observe(getViewLifecycleOwner(), result -> {
                showEvents(result, 1);
            });
        } else if (!categories.isEmpty() && !Objects.equals(fromDate, "") && !Objects.equals(toDate, "")) {
            eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(firstDate, endDate, categories).observe(getViewLifecycleOwner(), result -> {
                showEvents(result, 2);
            });
        } else if (!categories.isEmpty() && Objects.equals(fromDate, "") && Objects.equals(toDate, "")) {
            eventsAndPlacesViewModel.getCategoriesEventsLiveData(categories).observe(getViewLifecycleOwner(), result -> {
                showEvents(result, 3);
            });
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
        fragmentAllEventsBinding = null;
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showEvents(Result result, int typeOfQuery) {
        if (result.isSuccess()) {
            Log.i("SUCCESSO", "SUCCESSO GET EVENTS");

            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
            List<Events> fetchedEvents = eventsResponse.getEventsList();
            for(Events e : fetchedEvents){
                if(e.getCategory().equals("movies")){
                    Log.i(TAG,"CIAO");
                }
            }

            if (!eventsAndPlacesViewModel.isLoading()) {
                eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                this.eventsList.addAll(fetchedEvents);
                if (!eventsList.isEmpty()) {
                    //sortEvents(sortingParameter, eventsList);
                }
                eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                        this.eventsList.size());
                fragmentAllEventsBinding.progressBar.setVisibility(View.GONE);
            } else {
                Log.i(TAG, "IS LOADING");
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
                if (!eventsList.isEmpty()) {
                    sortEvents(sortingParameter, eventsList);
                }
                eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, eventsList.size());
            }
            fragmentAllEventsBinding.numberOfEvents.setText(String.valueOf(eventsList.size()));

        } else {
            Log.i("FALLITO", "FALLITO ALL EVENTS");

            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage(((Result.Error) result).getMessage()),
                    Snackbar.LENGTH_SHORT).show();
            fragmentAllEventsBinding.progressBar.setVisibility(View.GONE);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();

                if (isConnected && totalItemCount != eventsAndPlacesViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !eventsAndPlacesViewModel.isLoading()
                            ) &&
                                    eventsAndPlacesViewModel.getEventsResponseLiveData().getValue() != null &&
                                    eventsAndPlacesViewModel.getCurrentResults() != eventsAndPlacesViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> eventsListMutableLiveData;
                        switch (typeOfQuery) {
                            case 0:
                                eventsListMutableLiveData = eventsAndPlacesViewModel.getEventsResponseLiveData();
                                break;
                            case 1:
                                eventsListMutableLiveData = eventsAndPlacesViewModel.getEventsBetweenDatesLiveData(firstDate, endDate);
                                break;
                            case 2:
                                eventsListMutableLiveData = eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(firstDate, endDate, checkedCategories);
                                break;
                            case 3:
                                eventsListMutableLiveData = eventsAndPlacesViewModel.getCategoriesEventsLiveData(checkedCategories);
                                break;
                            default:
                                eventsListMutableLiveData = eventsAndPlacesViewModel.getEventsResponseLiveData();
                        }

                        if (eventsListMutableLiveData.getValue() != null &&
                                eventsListMutableLiveData.getValue().isSuccess()) {

                            eventsAndPlacesViewModel.setLoading(true);
                            eventsList.add(null);
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(eventsList.size(),
                                    eventsList.size() + 1);

                            int page = eventsAndPlacesViewModel.getPage() + 1;
                            eventsAndPlacesViewModel.setPage(page);
                            switch (typeOfQuery) {
                                case 0:
                                    eventsAndPlacesViewModel.fetchEvents(country, radius + "km@" + location, date, categories, sort, limit);
                                    break;
                                case 1:
                                    eventsAndPlacesViewModel.getEventsBetweenDatesLiveData(firstDate, endDate);
                                    break;
                                case 2:
                                    eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(firstDate, endDate, checkedCategories);
                                    break;
                                case 3:
                                    eventsAndPlacesViewModel.getCategoriesEventsLiveData(checkedCategories);
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }


}
