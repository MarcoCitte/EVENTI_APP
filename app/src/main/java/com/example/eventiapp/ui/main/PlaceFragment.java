package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.EVENTS_VIEW_TYPE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PlaceFragment extends Fragment {

    private com.example.eventiapp.databinding.FragmentSinglePlaceBinding fragmentSinglePlaceBinding;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private LayoutInflater inflater;

    MapView mMapView;
    private GoogleMap googleMap;

    private List<Events> eventsList;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    //private SharedPreferencesUtil sharedPreferencesUtil;

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

    public PlaceFragment() {
        // Required empty public constructor
    }

    public static PlaceFragment newInstance() {
        return new PlaceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsAndPlacesViewModel = new ViewModelProvider(requireActivity()).get(EventsAndPlacesViewModel.class);
        eventsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSinglePlaceBinding = com.example.eventiapp.databinding.FragmentSinglePlaceBinding.inflate(inflater, container, false);
        return fragmentSinglePlaceBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
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

        Place place = getArguments().getParcelable("place", Place.class);

        PlaceDetailsSource.fetchPlacePhotos(place.getImages(), false, new PlaceDetailsSource.PlacePhotosListener() {
            @Override
            public void onPlacePhotosListener(Bitmap bitmap) {
                fragmentSinglePlaceBinding.scrollViewImagesPlace.setVisibility(View.VISIBLE);
                if (bitmap != null) {
                    View viewPlacePhoto = inflater.inflate(R.layout.item_place_photo_maps, fragmentSinglePlaceBinding.galleryPhotos, false);
                    ImageView imagePlace = viewPlacePhoto.findViewById(R.id.imagePlace);
                    imagePlace.setImageBitmap(bitmap);
                    fragmentSinglePlaceBinding.galleryPhotos.addView(viewPlacePhoto);
                }
            }

            @Override
            public void onError(String message) {
                Log.i("ERROR", message);
            }
        });

        fragmentSinglePlaceBinding.placeName.setText(place.getName());
        fragmentSinglePlaceBinding.placeAddress.setText(place.getAddress());
        fragmentSinglePlaceBinding.phoneNumber.setText(place.getPhoneNumber());

        fragmentSinglePlaceBinding.imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.sharePlace(requireContext(),place);
            }
        });

        fragmentSinglePlaceBinding.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //POSTO PREFERITO
            }
        });

        //GOOGLE MAPS
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For dropping a marker at a point on the Map
                double[] location = place.getCoordinates();
                LatLng latLng = new LatLng(location[0], location[1]);
                googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()).snippet(place.getAddress()));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        LatLng position = marker.getPosition();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        googleMap.getMaxZoomLevel();
                        marker.showInfoWindow();

                        return true;
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latLng.latitude, latLng.longitude);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_events);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(eventsList,
                requireActivity().getApplication(), 0,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(view).navigate(R.id.action_placeFragment_to_eventFragment, bundle);
                    }

                    @Override
                    public void onExportButtonPressed(Events events) {

                    }

                    @Override
                    public void onShareButtonPressed(Events events) {
                        ShareUtils.shareEvent(requireContext(), events);
                    }


                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        //SETTA EVENTO COME PREFERITO
                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);

        String lastUpdate = "0";
        String id = place.getId();
        fragmentSinglePlaceBinding.progressBar.setVisibility(View.VISIBLE);

        eventsAndPlacesViewModel.getPlaceEventsLiveData(id).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Log.i("SUCCESSO", "SUCCESSO");

                    eventsList.clear();
                    EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                    List<Events> fetchedEvents = eventsResponse.getEventsList();

                    if (!eventsAndPlacesViewModel.isLoading()) {
                        eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                        this.eventsList.clear();
                        this.eventsList.addAll(fetchedEvents);
                        eventsRecyclerViewAdapter.notifyItemChanged(0, fetchedEvents.size());
                        fragmentSinglePlaceBinding.progressBar.setVisibility(View.GONE);
                        fragmentSinglePlaceBinding.placeEvents.setVisibility(View.VISIBLE);
                        fragmentSinglePlaceBinding.numberEventsTextView.setVisibility(View.VISIBLE);
                        fragmentSinglePlaceBinding.numberEventsTextView.setText(Integer.toString(eventsList.size()));

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
                } else {
                    Log.i("FALLITO", "FALLITO");

                    ErrorMessageUtil errorMessagesUtil =
                            new ErrorMessageUtil(requireActivity().getApplication());
                    Snackbar.make(view, errorMessagesUtil.
                                    getErrorMessage(((Result.Error) result).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                    fragmentSinglePlaceBinding.progressBar.setVisibility(View.GONE);
                }
            } else {
                //NON CI SONO EVENTI IN QUEL LOCALE
                fragmentSinglePlaceBinding.placeEvents.setVisibility(View.VISIBLE);
                fragmentSinglePlaceBinding.placeEvents.setText("There are no events");
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
                                    eventsAndPlacesViewModel.getPlaceEventsLiveData(id).getValue() != null &&
                                    eventsAndPlacesViewModel.getCurrentResults() != eventsAndPlacesViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> eventsListMutableLiveData = eventsAndPlacesViewModel.getPlaceEventsLiveData(id);

                        if (eventsListMutableLiveData.getValue() != null &&
                                eventsListMutableLiveData.getValue().isSuccess()) {

                            eventsAndPlacesViewModel.setLoading(true);
                            eventsList.add(null);
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(eventsList.size(),
                                    eventsList.size() + 1);

                            int page = eventsAndPlacesViewModel.getPage() + 1;
                            eventsAndPlacesViewModel.setPage(page);
                            eventsAndPlacesViewModel.getPlaceEventsLiveData(id);
                        }
                    }
                }
            }
        });


        NavBackStackEntry navBackStackEntry = Navigation.
                findNavController(view).getPreviousBackStackEntry();

        if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().

                        getId() == R.id.homeFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.homeFragment).setChecked(true);
        } else if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().

                        getId() == R.id.myEventsFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.myEventsFragment).setChecked(true);
        } else if (navBackStackEntry != null &&
                navBackStackEntry.getDestination().

                        getId() == R.id.mapsFragment) {
            ((BottomNavigationView) requireActivity().findViewById(R.id.bottomNavigationView)).
                    getMenu().findItem(R.id.mapsFragment).setChecked(true);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventsAndPlacesViewModel.setFirstLoading(true);
        eventsAndPlacesViewModel.setLoading(false);
        mMapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSinglePlaceBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
