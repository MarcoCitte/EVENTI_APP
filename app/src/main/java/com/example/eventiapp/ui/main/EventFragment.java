package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentEventBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {

    private FragmentEventBinding fragmentEventBinding;
    private EventsViewModel eventsViewModel;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance() {
        return new EventFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEventBinding = FragmentEventBinding.inflate(inflater, container, false);
        return fragmentEventBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    Navigation.findNavController(requireView()).navigateUp();
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        Events events = getArguments().getParcelable("event", Events.class);

        fragmentEventBinding.eventTitle.setText(events.getTitle());
        fragmentEventBinding.eventCategory.setText(events.getCategory());
        //fragmentEventBinding.eventTimezone.setText(events.getTimezone());
        fragmentEventBinding.eventDescription.setText(events.getDescription());
        String date = "FROM: " + events.getStart() + " TO: " + events.getEnd() + "(" + events.getTimezone() +")";
        fragmentEventBinding.eventDate.setText(date);
        if (!events.getPlaces().isEmpty()) {
            fragmentEventBinding.eventLocation.setText((CharSequence) events.getPlaces().get(0).getAddress());
        } else {
            fragmentEventBinding.eventLocation.setVisibility(View.GONE);
        }


        NavBackStackEntry navBackStackEntry = Navigation.
                findNavController(view).getPreviousBackStackEntry();

        if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().getId() == R.id.homeFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.homeFragment).setChecked(true);
        } else if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().getId() == R.id.myEventsFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.myEventsFragment).setChecked(true);
        } else if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().getId() == R.id.mapsFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.mapsFragment).setChecked(true);
        }
    }

}
