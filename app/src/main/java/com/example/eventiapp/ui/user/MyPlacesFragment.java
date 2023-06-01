package com.example.eventiapp.ui.user;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.adapter.PlacesRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentMyEventsBinding;
import com.example.eventiapp.databinding.FragmentMyPlacesBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModelFactory;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MyPlacesFragment extends Fragment {

    private static final String TAG = MyPlacesFragment.class.getSimpleName();
    private FragmentMyPlacesBinding fragmentMyPlacesBinding;

    private List<Place> placeList;
    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    private LinearLayoutManager layoutManagerMyPlaces;
    private LinearLayoutManager layoutManagerFavoritePlaces;


    public MyPlacesFragment() {
        // Required empty public constructor
    }

    public static MyPlacesFragment newInstance() {
        return new MyPlacesFragment();
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

        placeList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyPlacesBinding = FragmentMyPlacesBinding.inflate(inflater, container, false);
        return fragmentMyPlacesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        layoutManagerMyPlaces =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);
        layoutManagerFavoritePlaces =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        placesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(placeList,
                requireActivity().getApplication(), 2, new PlacesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onPlacesItemClick(Place place) {

            }

            @Override
            public void onShareButtonPressed(Place place) {

            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                placeList.get(position).setFavorite(false);
                eventsAndPlacesViewModel.removeFromFavorite(placeList.get(position));
            }

            @Override
            public void onModePlaceButtonPressed(Place place) {

            }

            @Override
            public void onDeletePlaceButtonPressed(Place place) {

            }
        });
        fragmentMyPlacesBinding.recyclerViewFavoritePlaces.setLayoutManager(layoutManagerFavoritePlaces);
        fragmentMyPlacesBinding.recyclerViewFavoritePlaces.setAdapter(placesRecyclerViewAdapter);

        fragmentMyPlacesBinding.progressBarFavoritePlaces.setVisibility(View.VISIBLE);

        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoading = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING);


        eventsAndPlacesViewModel.
                getFavoritePlacesLiveData(isFirstLoading).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            placeList.clear();
                            placeList.addAll(((Result.PlacesResponseSuccess) result).getData());
                            placesRecyclerViewAdapter.notifyDataSetChanged();
                            if (isFirstLoading) {
                                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                        SHARED_PREFERENCES_FIRST_LOADING, false);
                            }
                        } else {
                            ErrorMessageUtil errorMessagesUtil =
                                    new ErrorMessageUtil(requireActivity().getApplication());
                            Snackbar.make(view, errorMessagesUtil.
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                        fragmentMyPlacesBinding.progressBarFavoritePlaces.setVisibility(View.GONE);
                    }
                });




        fragmentMyPlacesBinding.createPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_addPlaceFragment);
            }
        });
    }
}