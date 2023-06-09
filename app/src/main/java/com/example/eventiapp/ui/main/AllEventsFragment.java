package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.REQUEST_CODE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.eventsAndPlaces.IRepositoryWithLiveData;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
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
    String categoriesString = "conferences,expos,concerts,festivals,performing-arts,sports,community";
    int limit = 5000;
    String lastUpdate = "0";
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
        boolean showForYou=false;
        boolean sortBool=false;

        Bundle bundle = getArguments();
        if (bundle != null && (!Objects.equals(getArguments().getString("sort"), null))) {
            if(getArguments().getString("sort").equals("cfy")){
                showForYou=true;
            }else {
                sortBool = true;
                String sort = getArguments().getString("sort");
                sortingParameter = sort;
                for (int i = 0; i < listItemsSort.length; i++) {
                    if (sortingParameter.equals(listItemsSort[i])) {
                        lastSelectedSortingParameter = i;
                    }
                }
            }
        }else {
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
                        //Navigation.findNavController(view).navigate(R.id.action_containerEventsPlacesCalendar_to_eventFragment, bundle);
                        NavController navController = Navigation.findNavController(requireView());
                        NavDestination currentDestination = navController.getCurrentDestination();
                        if (currentDestination != null && currentDestination.getId() == R.id.containerEventsPlacesCalendar) {
                            navController.navigate(R.id.action_containerEventsPlacesCalendar_to_eventFragment, bundle);
                        } else {
                            navController.navigate(R.id.action_searchFragment_to_eventFragment, bundle);
                        }
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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);

        fragmentAllEventsBinding.progressBar.setVisibility(View.VISIBLE);

        //eventsAndPlacesViewModel.deleteEvents(); //IN QUESTO MODO MI CARICA SEMPRE EVENTI NUOVI A PARTIRE DAL GIORNO CORRENTE

        eventsAndPlacesViewModel.getAllCategories().observe(getViewLifecycleOwner(), result -> {
            if (!result.isEmpty()) {
                allCategories = result;
            }
        });
        if(showForYou){
            eventsAndPlacesViewModel.getFavoriteCategoryEventsLiveData().observe(getViewLifecycleOwner(),result -> {
                showEvents(result);
            });
        }else if(sortBool){
            eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categoriesString, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
                sortEvents(sortingParameter,eventsList);
            });
        }else {
            eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categoriesString, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
            });
        }

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
            case "Latest date":
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
        firstDate = fromDate;
        endDate = toDate;
        if (categories == null || categories.isEmpty()) {
            checkedCategories = Collections.emptyList();
        } else {
            checkedCategories = categories;
        }
        if (checkedCategories.isEmpty() && Objects.equals(fromDate, "") && Objects.equals(toDate, "")) {
            //MOSTRA TUTTI GLI EVENTI
            eventsAndPlacesViewModel.getEvents(country, radius + "km@" + location, date, categoriesString, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
            });
        } else if (checkedCategories == allCategories && !Objects.equals(fromDate, "") && !Objects.equals(toDate, "")) {
            //MOSTRA EVENTI TUTTE CATEGORIE TRA DUE DATE
            eventsAndPlacesViewModel.getEventsBetweenDatesLiveData(fromDate, toDate).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
            });
        } else if (checkedCategories != allCategories && !Objects.equals(fromDate, "") && !Objects.equals(toDate, "")) {
            //MOSTRA EVENTI DI CERTE CATEGORIE TRA DUE DATE
            eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(firstDate, endDate, categories).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
            });
        } else if (checkedCategories != allCategories && Objects.equals(fromDate, "") && Objects.equals(toDate, "")) {
            //MOSTRA TUTTI GLI EVENTI DI QUELLA CATEGORIA/E IN TUTTE LE DATE POSSIBILI
            eventsAndPlacesViewModel.getCategoriesEventsLiveData(categories).observe(getViewLifecycleOwner(), result -> {
                showEvents(result);
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

    private void showEvents(Result result) {
        if (result.isSuccess()) {

            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
            List<Events> fetchedEvents = eventsResponse.getEventsList();

            if (!eventsAndPlacesViewModel.isLoading()) {
                eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                this.eventsList.clear();
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


    }


}
