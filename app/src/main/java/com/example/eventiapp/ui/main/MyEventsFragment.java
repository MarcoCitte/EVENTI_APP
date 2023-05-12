package com.example.eventiapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsListAdapter;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentCategoryBinding;
import com.example.eventiapp.databinding.FragmentMyEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {

    private static final String TAG = MyEventsFragment.class.getSimpleName();

    private List<Events> eventsList;
    private EventsListAdapter eventsListAdapter;
    private ProgressBar progressBar;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private UserViewModel userViewModel;


    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static MyEventsFragment newInstance() {
        return new MyEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventsList = new ArrayList<>();
        eventsAndPlacesViewModel = new ViewModelProvider(requireActivity()).get(EventsAndPlacesViewModel.class);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //eventsViewModel.deleteEvents();

        Button buttonLogout = view.findViewById(R.id.logout_b);
        buttonLogout.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    Navigation.findNavController(view).navigate(
                            R.id.action_myEventsFragment_to_welcomeActivity);
                    requireActivity().finish();
                } else {
                    Snackbar.make(view,
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                // It adds the menu item in the toolbar
                menuInflater.inflate(R.menu.top_app_bar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.delete) {
                    eventsAndPlacesViewModel.deleteEvents();
                }
                return false;
            }
            // Use getViewLifecycleOwner() to avoid that the listener
            // associated with a menu icon is called twice
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        progressBar = view.findViewById(R.id.progress_bar);

        ListView listViewFavEvents = view.findViewById(R.id.listview_fav_events);

        eventsListAdapter =
                new EventsListAdapter(requireContext(), R.layout.favorite_events_list_item, eventsList,
                        events -> {
                            events.setFavorite(false);
                            eventsAndPlacesViewModel.removeFromFavorite(events);
                        });
        listViewFavEvents.setAdapter(eventsListAdapter);

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                Constants.SHARED_PREFERENCES_FIRST_LOADING);

        // Observe the LiveData associated with the MutableLiveData containing the favorite news
        // returned by the method getFavoriteNewsLiveData() of NewsViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        eventsAndPlacesViewModel.
                getFavoriteEventsLiveData(isFirstLoading).
                observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    eventsList.clear();
                    eventsList.addAll(((Result.EventsResponseSuccess)result).getData().getEventsList());
                    eventsListAdapter.notifyDataSetChanged();
                    if (isFirstLoading) {
                        sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                Constants.SHARED_PREFERENCES_FIRST_LOADING, false);
                    }
                } else {
                    ErrorMessageUtil errorMessagesUtil =
                            new ErrorMessageUtil(requireActivity().getApplication());
                    Snackbar.make(view, errorMessagesUtil.
                                    getErrorMessage(((Result.Error)result).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        /*
        listViewFavEvents.setOnItemClickListener((parent, view1, position, id) -> {
            FavoriteEventsFragmentDirections.ActionFavoriteNewsFragmentToNewsDetailFragment action =
                    FavoriteEventsFragmentDirections.
                            actionFavoriteNewsFragmentToNewsDetailFragment(eventsList.get(position));
            Navigation.findNavController(view).navigate(action);
        });

         */
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventsAndPlacesViewModel.deleteEvents();
    }
}
