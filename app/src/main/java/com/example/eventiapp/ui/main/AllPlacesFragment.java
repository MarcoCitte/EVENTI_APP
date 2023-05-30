package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

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
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.PlacesRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentPlacesBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllPlacesFragment extends Fragment {


    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentPlacesBinding fragmentPlacesBinding;

    private List<Place> placesList;
    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    //private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of places
    private int lastVisibleItem; // The position of the last visible place item
    private int visibleItemCount; // Number or total visible place items

    // Based on this value, the process of loading more places is anticipated or postponed
    private final int threshold = 1;

    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private String[] listItemsSort;


    public AllPlacesFragment() {
        // Required empty public constructor
    }

    public static AllPlacesFragment newInstance(Bundle bundle) {
        AllPlacesFragment fragment = new AllPlacesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IRepositoryWithLiveData repositoryWithLiveData =
                ServiceLocator.getInstance().getRepository(
                        requireActivity().getApplication()
                );

        if (repositoryWithLiveData != null) {
            eventsAndPlacesViewModel = new ViewModelProvider(
                    requireActivity(),
                    new EventsAndPlacesViewModelFactory(repositoryWithLiveData)).get(EventsAndPlacesViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
        }
        placesList = new ArrayList<>();
        listItemsSort = requireContext().getResources().getStringArray(R.array.sorting_parameters_places);

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
        String date = DateUtils.currentDate();
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

        placesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(placesList,
                requireActivity().getApplication(), 2,
                new PlacesRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onPlacesItemClick(Place place) {
                        //VAI AI DETTAGLI DEL POSTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("place", place);
                        //Navigation.findNavController(view).navigate(R.id.action_containerEventsPlacesCalendar_to_placeFragment, bundle);
                        NavController navController = Navigation.findNavController(requireView());
                        NavDestination currentDestination = navController.getCurrentDestination();
                        if (currentDestination != null && currentDestination.getId() == R.id.containerEventsPlacesCalendar) {
                            navController.navigate(R.id.action_containerEventsPlacesCalendar_to_placeFragment, bundle);
                        } else {
                            navController.navigate(R.id.action_searchFragment_to_placeFragment, bundle);
                        }
                    }

                    @Override
                    public void onShareButtonPressed(Place place) {
                        ShareUtils.sharePlace(requireContext(),place);
                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {

                    }

                    @Override
                    public void onModePlaceButtonPressed(Place place) {

                    }

                    @Override
                    public void onDeletePlaceButtonPressed(Place place) {

                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(placesRecyclerViewAdapter);

        String lastUpdate = "0";

        fragmentPlacesBinding.progressBar.setVisibility(View.VISIBLE);


        eventsAndPlacesViewModel.getPlaces().observe(getViewLifecycleOwner(), result -> {

            if (result != null) {
                Log.i("SUCCESSO", "SUCCESSO");

                List<Place> fetchedPlaces = new ArrayList<>(result);

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
                    fragmentPlacesBinding.progressBar.setVisibility(View.GONE);
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
                fragmentPlacesBinding.numberOfEvents.setText(String.valueOf(placesList.size()));
            } else {
                Log.i("FALLITO", "FALLITO");

                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(view, errorMessagesUtil.
                                getErrorMessage("ERRORE"),
                        Snackbar.LENGTH_SHORT).show();
                fragmentPlacesBinding.progressBar.setVisibility(View.GONE);
            }
        });

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
                                    eventsAndPlacesViewModel.getPlacesResponseLiveData().getValue() != null &&
                                    eventsAndPlacesViewModel.getCurrentResults() != eventsAndPlacesViewModel.getTotalResults()
                    ) {
                        MutableLiveData<List<Place>> placeListMutableLiveData = eventsAndPlacesViewModel.getPlacesResponseLiveData();

                        if (placeListMutableLiveData.getValue() != null) {

                            eventsAndPlacesViewModel.setLoading(true);
                            placesList.add(null);
                            placesRecyclerViewAdapter.notifyItemRangeInserted(placesList.size(),
                                    placesList.size() + 1);

                            int page = eventsAndPlacesViewModel.getPage() + 1;
                            eventsAndPlacesViewModel.setPage(page);
                            eventsAndPlacesViewModel.fetchPlaces();
                        }
                    }
                }
            }
        });

        //SORTING
        fragmentPlacesBinding.sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSorting();
            }
        });

    }

    public void showSorting() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle("ORDER BY")
                .setSingleChoiceItems(listItemsSort, lastSelectedSortingParameter, (dialog, i) -> {
                    sortingParameter = listItemsSort[i];
                    lastSelectedSortingParameter = i;
                    if (!placesList.isEmpty()) {
                        sortPlaces(sortingParameter, placesList);
                    }
                }).setNegativeButton(R.string.cancel_text, (dialogInterface, i) -> {
                }).show();
    }

    public void sortPlaces(String sortingParameter, List<Place> placesList) {
        switch (sortingParameter) {
            case "Alphabet (A-Z)":
            case "Alfabetico (A-Z)":
                Collections.sort(placesList, new Place.SortByAlphabetAZ());
                break;
            case "Alphabet (Z-A)":
            case "Alfabetico (Z-A)":
                Collections.sort(placesList, new Place.SortByAlphabetZA());
                break;
        }
        placesRecyclerViewAdapter.notifyDataSetChanged();
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
        fragmentPlacesBinding = null;
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
