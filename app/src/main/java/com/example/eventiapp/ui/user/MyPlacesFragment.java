package com.example.eventiapp.ui.user;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING_FAVORITEPLACES;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING_MYPLACES;

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

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.adapter.PlacesRecyclerViewAdapter;
import com.example.eventiapp.databinding.FragmentMyEventsBinding;
import com.example.eventiapp.databinding.FragmentMyPlacesBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
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
    private List<Place> myPlacesList;

    private PlacesRecyclerViewAdapter placesRecyclerViewAdapter;
    private PlacesRecyclerViewAdapter myPlacesRecyclerViewAdapter;

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
        myPlacesList = new ArrayList<>();

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
                Bundle bundle = new Bundle();
                bundle.putParcelable("place", place);
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_placeFragment,bundle);
            }

            @Override
            public void onShareButtonPressed(Place place) {
                ShareUtils.sharePlace(requireContext(), place);
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

        myPlacesRecyclerViewAdapter = new PlacesRecyclerViewAdapter(myPlacesList,
                requireActivity().getApplication(), 6, new PlacesRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onPlacesItemClick(Place place) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("place", place);
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_placeFragment,bundle);
            }

            @Override
            public void onShareButtonPressed(Place place) {
                ShareUtils.sharePlace(requireContext(), place);
            }

            @Override
            public void onFavoriteButtonPressed(int position) {
                myPlacesList.get(position).setFavorite(false);
                eventsAndPlacesViewModel.removeFromFavorite(myPlacesList.get(position));
            }

            @Override
            public void onModePlaceButtonPressed(Place place) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("place", place);
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_editPlaceFragment, bundle);
            }

            @Override
            public void onDeletePlaceButtonPressed(Place place) {
                eventsAndPlacesViewModel.deleteMyPlace(place);
            }
        });


        fragmentMyPlacesBinding.recyclerViewFavoritePlaces.setLayoutManager(layoutManagerFavoritePlaces);
        fragmentMyPlacesBinding.recyclerViewFavoritePlaces.setAdapter(placesRecyclerViewAdapter);

        fragmentMyPlacesBinding.recyclerViewMyPlaces.setLayoutManager(layoutManagerMyPlaces);
        fragmentMyPlacesBinding.recyclerViewMyPlaces.setAdapter(myPlacesRecyclerViewAdapter);

        fragmentMyPlacesBinding.progressBarFavoritePlaces.setVisibility(View.VISIBLE);
        fragmentMyPlacesBinding.progressBarMyPlaces.setVisibility(View.VISIBLE);


        SharedPreferencesUtil sharedPreferencesUtil =
                new SharedPreferencesUtil(requireActivity().getApplication());

        boolean isFirstLoadingFavoritePlaces = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING_FAVORITEPLACES);
        boolean isFirstLoadingMyPlaces = sharedPreferencesUtil.readBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                SHARED_PREFERENCES_FIRST_LOADING_MYPLACES);

        eventsAndPlacesViewModel.
                getFavoritePlacesLiveData(isFirstLoadingFavoritePlaces).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            List<Place> fetchedPlaces = ((Result.PlacesResponseSuccess) result).getData();
                            if (!fetchedPlaces.isEmpty()) {
                                placeList.clear();
                                placeList.addAll(fetchedPlaces);
                                placesRecyclerViewAdapter.notifyDataSetChanged();
                                if (isFirstLoadingFavoritePlaces) {
                                    sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                            SHARED_PREFERENCES_FIRST_LOADING_FAVORITEPLACES, false);
                                }
                            } else {
                                placeList.clear();
                                placesRecyclerViewAdapter.notifyDataSetChanged();
                                fragmentMyPlacesBinding.textViewNoFavoritePlaces.setVisibility(View.VISIBLE);
                                fragmentMyPlacesBinding.textViewNoFavoritePlaces1.setVisibility(View.VISIBLE);
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


        eventsAndPlacesViewModel.
                getMyPlacesLiveData(isFirstLoadingMyPlaces).
                observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        Log.e(TAG, "onViewCreated: myPlacesListSize: " + result.size());
                        List<Place> fetchedPlaces = result;
                        if (!fetchedPlaces.isEmpty()) {
                            myPlacesList.clear();
                            myPlacesList.addAll(fetchedPlaces);
                            myPlacesRecyclerViewAdapter.notifyDataSetChanged();
                            if (isFirstLoadingMyPlaces) {
                                sharedPreferencesUtil.writeBooleanData(Constants.SHARED_PREFERENCES_FILE_NAME,
                                        SHARED_PREFERENCES_FIRST_LOADING_MYPLACES, false);
                            }
                        } else {
                            myPlacesList.clear();
                            myPlacesRecyclerViewAdapter.notifyDataSetChanged();
                            fragmentMyPlacesBinding.textViewNoMyPlaces.setVisibility(View.VISIBLE);
                            fragmentMyPlacesBinding.textViewNoMyPlaces1.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG, "onViewCreated: result = null");
                    }
                    fragmentMyPlacesBinding.progressBarMyPlaces.setVisibility(View.GONE);

                });

        fragmentMyPlacesBinding.createPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_addPlaceFragment);
            }
        });
    }
}