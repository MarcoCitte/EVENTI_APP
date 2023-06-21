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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentUserEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserEventsFragment extends Fragment {

    private static final String TAG = UserEventsFragment.class.getSimpleName();

    private FragmentUserEventsBinding fragmentUserEventsBinding;

    private List<Events> eventsList;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private String[] listItemsSort;

    String lastUpdate = "0";

    //private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;


    public UserEventsFragment() {
        // Required empty public constructor
    }

    public static UserEventsFragment newInstance(Bundle bundle) {
        UserEventsFragment fragment = new UserEventsFragment();
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
        listItemsSort = requireContext().getResources().getStringArray(R.array.sorting_parameters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentUserEventsBinding = FragmentUserEventsBinding.inflate(inflater, container, false);
        return fragmentUserEventsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        Navigation.findNavController(view).navigate(R.id.action_userEventsFragment_to_eventFragment, bundle);
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

        fragmentUserEventsBinding.progressBar.setVisibility(View.VISIBLE);

        eventsAndPlacesViewModel.getUserCreatedEvents(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            showEvents(result);
        });

        //FILTRI

        fragmentUserEventsBinding.sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSorting();
            }
        });

    }

    public void showSorting() {
        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.order_by)
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
    public void onDestroy() {
        super.onDestroy();
        eventsAndPlacesViewModel.setFirstLoading(true);
        eventsAndPlacesViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUserEventsBinding = null;
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
                fragmentUserEventsBinding.progressBar.setVisibility(View.GONE);
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
                if (!eventsList.isEmpty()) {
                    sortEvents(sortingParameter, eventsList);
                }
                eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, eventsList.size());
            }
            fragmentUserEventsBinding.numberOfEvents.setText(String.valueOf(eventsList.size()));

        } else {
            ErrorMessageUtil errorMessagesUtil =
                    new ErrorMessageUtil(requireActivity().getApplication());
            Snackbar.make(requireView(), errorMessagesUtil.
                            getErrorMessage(((Result.Error) result).getMessage()),
                    Snackbar.LENGTH_SHORT).show();
            fragmentUserEventsBinding.progressBar.setVisibility(View.GONE);
        }


    }

}