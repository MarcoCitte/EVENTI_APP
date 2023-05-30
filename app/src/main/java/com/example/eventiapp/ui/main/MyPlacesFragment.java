package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
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
import com.example.eventiapp.adapter.PlacesListAdapter;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MyPlacesFragment extends Fragment {

    private List<Place> placesList;
    private PlacesListAdapter placesListAdapter;
    private ProgressBar progressBar;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    public MyPlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placesList = new ArrayList<>();
        eventsAndPlacesViewModel = new ViewModelProvider(requireActivity()).get(EventsAndPlacesViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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
                    //eventsAndPlacesViewModel.deleteEvents(); METODO SBAGLIATO
                }
                return false;
            }
            // Use getViewLifecycleOwner() to avoid that the listener
            // associated with a menu icon is called twice
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        progressBar = view.findViewById(R.id.progress_bar2);

        ListView listViewFavPlaces = view.findViewById(R.id.listview_fav_places);

        placesListAdapter =
                new PlacesListAdapter(requireContext(), R.layout.favorite_place_list_item, placesList,
                        place -> {
                            Log.e("TAG", "deleteFE2: " + place.hashCode());
                            place.setFavorite(false);
                            eventsAndPlacesViewModel.removeFromFavorite(place);
                        });
        listViewFavPlaces.setAdapter(placesListAdapter);

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING);

        // Observe the LiveData associated with the MutableLiveData containing the favorite news
        // returned by the method getFavoriteNewsLiveData() of NewsViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        eventsAndPlacesViewModel.
                getFavoritePlacesLiveData(isFirstLoading).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            placesList.clear();
                            placesList.addAll(((Result.PlacesResponseSuccess)result).getData());
                            placesListAdapter.notifyDataSetChanged();
                            if (isFirstLoading) {
                                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                        SHARED_PREFERENCES_FIRST_LOADING, false);
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


        listViewFavPlaces.setOnItemClickListener((parent, view1, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("place", placesList.get(position));
            Navigation.findNavController(view).navigate(R.id.action_myEventsFragment_to_eventFragment, bundle);
        });




    }
}