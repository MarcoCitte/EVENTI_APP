package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.annotation.SuppressLint;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentEventsInADateBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EventsInADateFragment extends Fragment {

    public EventsInADateFragment() {
    }

    private static final String TAG = EventsInADateFragment.class.getSimpleName();

    private FragmentEventsInADateBinding fragmentEventsInADateBinding;

    private List<Events> eventsList;
    private List<String> categoriesInADate;
    private List<String> checkedCategories;
    private String date;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    //private SharedPreferencesUtil sharedPreferencesUtil;

    private String sortingParameter;
    private int lastSelectedSortingParameter;
    private String[] listItemsSort;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

    public static AllEventsFragment newInstance() {
        return new AllEventsFragment();
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
        categoriesInADate = new ArrayList<>();
        checkedCategories = new ArrayList<>();
        listItemsSort = requireContext().getResources().getStringArray(R.array.sorting_parameters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEventsInADateBinding = FragmentEventsInADateBinding.inflate(inflater, container, false);
        return fragmentEventsInADateBinding.getRoot();
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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager =
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
                        Navigation.findNavController(view).navigate(R.id.action_eventsInADateFragment_to_eventFragment, bundle);
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
                        //SETTA EVENTO COME PREFERITO
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
        assert getArguments() != null;
        date = getArguments().getString("date");

        fragmentEventsInADateBinding.progressBar.setVisibility(View.VISIBLE);

        //CATEGORIE DI EVENTI IN QUELLA DATA
        eventsAndPlacesViewModel.getCategoriesInADate(date).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                categoriesInADate.clear();
                categoriesInADate = result;
                setChips(categoriesInADate);
            }
        });

        eventsAndPlacesViewModel.getEventsInADateLiveData(date).observe(getViewLifecycleOwner(), result -> {
            showEvents(result);
        });

        fragmentEventsInADateBinding.sortingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSorting();
            }
        });


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
        fragmentEventsInADateBinding = null;
    }


    private void setChips(List<String> categoriesOfthatDate) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(16);
        fragmentEventsInADateBinding.chipsLinearLayout.removeAllViews();
        for (int i = 0; i < categoriesOfthatDate.size(); i++) {
            Chip chip = new Chip(getContext());
            chip.setText(categoriesOfthatDate.get(i));
            chip.setChipBackgroundColorResource(R.color.colorBackgroundSecondary);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setCloseIconVisible(false);
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedCategories.add((String) buttonView.getText());
                        chip.setChipBackgroundColorResource(R.color.colorBackground);
                        eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(date, date, checkedCategories).observe(getViewLifecycleOwner(), result -> {
                            showEvents(result);
                        });
                    } else {
                        checkedCategories.remove(buttonView.getText());
                        chip.setChipBackgroundColorResource(R.color.colorBackgroundSecondary);
                        if (checkedCategories.isEmpty()) { //SE NON è SELEZIONATO PIù NIENTE RIMETTE TUTTO
                            eventsAndPlacesViewModel.getEventsInADateLiveData(date).observe(getViewLifecycleOwner(), result -> {
                                showEvents(result);
                            });
                        } else {
                            eventsAndPlacesViewModel.getCategoryEventsBetweenDatesLiveData(date, date, checkedCategories).observe(getViewLifecycleOwner(), result -> {
                                showEvents(result);
                            });
                        }
                    }
                }
            });
            fragmentEventsInADateBinding.chipsLinearLayout.addView(chip, params);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showEvents(Result result) {
        if (result != null) {
            if (result.isSuccess()) {
                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();
                if (!eventsAndPlacesViewModel.isLoading()) {
                    eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                    this.eventsList.clear();
                    this.eventsList.addAll(fetchedEvents);
                    eventsRecyclerViewAdapter.notifyItemChanged(0, fetchedEvents.size());
                    fragmentEventsInADateBinding.progressBar.setVisibility(View.GONE);
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
                fragmentEventsInADateBinding.textViewDate.setText(getResources().getString(R.string.eventsinthisdate) + " " + date);
                fragmentEventsInADateBinding.numberOfEvents.setText(" " + eventsList.size());
            } else {
                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(requireView(), errorMessagesUtil.
                                getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
                fragmentEventsInADateBinding.progressBar.setVisibility(View.GONE);
            }
        } else {
            fragmentEventsInADateBinding.textViewDate.setText(getResources().getString(R.string.noeventsinthisdate)  + " " + date);
            fragmentEventsInADateBinding.numberOfEvents.setText(" 0");
        }
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

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
