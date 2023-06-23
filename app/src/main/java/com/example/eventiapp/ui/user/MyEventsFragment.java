package com.example.eventiapp.ui.user;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentMyEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModelFactory;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MyEventsFragment extends Fragment {

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private FragmentMyEventsBinding fragmentMyEventsBinding;

    private List<Events> eventsList;
    private List<Events> myEventsList;

    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private EventsRecyclerViewAdapter myEventsRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    private LinearLayoutManager layoutManagerMyEvents;
    private LinearLayoutManager layoutManagerFavoriteEvents;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static MyEventsFragment newInstance() {
        return new MyEventsFragment();
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
        myEventsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyEventsBinding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return fragmentMyEventsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        layoutManagerMyEvents =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);
        layoutManagerFavoriteEvents =
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
                        Navigation.findNavController(requireView()
                        ).navigate(R.id.action_containerMyEventsAndPlaces_to_eventFragment, bundle);
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
                        eventsList.get(position).setFavorite(false);
                        eventsAndPlacesViewModel.removeFromFavorite(eventsList.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {

                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {

                    }
                });

        myEventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(myEventsList,
                requireActivity().getApplication(), 5,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(requireView()
                        ).navigate(R.id.action_containerMyEventsAndPlaces_to_eventFragment, bundle);
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
                        myEventsList.get(position).setFavorite(false);
                        eventsAndPlacesViewModel.removeFromFavorite(myEventsList.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_editEventFragment, bundle);
                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {
                        eventsAndPlacesViewModel.deleteMyEvent(events);
                    }
                });

        fragmentMyEventsBinding.recyclerViewFavoriteEvents.setLayoutManager(layoutManagerFavoriteEvents);
        fragmentMyEventsBinding.recyclerViewFavoriteEvents.setAdapter(eventsRecyclerViewAdapter);

        fragmentMyEventsBinding.recyclerViewMyEvents.setLayoutManager(layoutManagerMyEvents);
        fragmentMyEventsBinding.recyclerViewMyEvents.setAdapter(myEventsRecyclerViewAdapter);

        fragmentMyEventsBinding.progressBarFavoriteEvents.setVisibility(View.VISIBLE);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING);

        eventsAndPlacesViewModel.
                getFavoriteEventsLiveData(isFirstLoading).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                            List<Events> fetchedEvents = eventsResponse.getEventsList();
                            if (!fetchedEvents.isEmpty()) {
                                eventsList.clear();
                                eventsList.addAll(fetchedEvents);
                                eventsRecyclerViewAdapter.notifyDataSetChanged();
                                if (isFirstLoading) {
                                    sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                            SHARED_PREFERENCES_FIRST_LOADING, false);
                                }
                            } else {
                                eventsList.clear();
                                eventsRecyclerViewAdapter.notifyDataSetChanged();
                                fragmentMyEventsBinding.textViewNoFavoriteEvents.setVisibility(View.VISIBLE);
                                fragmentMyEventsBinding.textViewNoFavoriteEvents1.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ErrorMessageUtil errorMessagesUtil =
                                    new ErrorMessageUtil(requireActivity().getApplication());
                            Snackbar.make(view, errorMessagesUtil.
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        fragmentMyEventsBinding.progressBarFavoriteEvents.setVisibility(View.GONE);
                    }
                });

        eventsAndPlacesViewModel.
                getMyEventsLiveData(isFirstLoading).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                            List<Events> fetchedEvents = eventsResponse.getEventsList();
                            if (!fetchedEvents.isEmpty()) {
                                myEventsList.clear();
                                myEventsList.addAll(fetchedEvents);
                                myEventsRecyclerViewAdapter.notifyDataSetChanged();
                                if (isFirstLoading) {
                                    sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                            SHARED_PREFERENCES_FIRST_LOADING, false);
                                }
                            } else {
                                myEventsList.clear();
                                myEventsRecyclerViewAdapter.notifyDataSetChanged();
                                fragmentMyEventsBinding.textViewNoMyEvents.setVisibility(View.VISIBLE);
                                fragmentMyEventsBinding.textViewNoMyEvents1.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ErrorMessageUtil errorMessagesUtil =
                                    new ErrorMessageUtil(requireActivity().getApplication());
                            Snackbar.make(view, errorMessagesUtil.
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        fragmentMyEventsBinding.progressBarFavoriteEvents.setVisibility(View.GONE);
                    }
                });

        fragmentMyEventsBinding.createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_addEventFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
