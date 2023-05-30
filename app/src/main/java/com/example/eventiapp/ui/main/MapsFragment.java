package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.ShareUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MapsFragment extends Fragment {

    private static final String TAG = MapsFragment.class.getSimpleName();

    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private List<com.example.eventiapp.model.Place> placesList;
    private List<Events> placeEventsList;
    GoogleMap myGoogleMap;
    private BottomSheetBehavior mBottomSheetBehavior1;
    View bottomSheet;
    LinearLayout tapactionlayout;
    LayoutInflater inflater;
    private ImageView carImageView;
    private ImageView mapsImageView;
    private ImageView callImageView;
    private ImageView favoriteImageView;
    private LinearLayout galleryPhotos;
    private HorizontalScrollView scrollViewImagesPlace;
    private TextView placeTextView;
    private TextView addressTextView;
    private TextView numberOfEvents;

    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private ProgressBar progressBar;
    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items
    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            myGoogleMap = googleMap;
            UiSettings mUiSettings = myGoogleMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setMapToolbarEnabled(true);


            LatLng bicocca = new LatLng(45.51851, 9.2075123);
           /*
            myGoogleMap.addMarker(new MarkerOptions().position(bicocca).title("Marker in Bicocca"));
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(bicocca));
            */

            float zoomLevel = 13.0f; //This goes up to 21
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bicocca, zoomLevel));

            int count = 0;

            myGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @NonNull
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.info_window_maps, null);
                    TextView textViewName = v.findViewById(R.id.place_name);
                    TextView textViewAddress = v.findViewById(R.id.place_address);
                    String[] parts = Objects.requireNonNull(marker.getTitle()).split(":");
                    String namePlace = parts[0];
                    textViewName.setText(namePlace);
                    textViewAddress.setText(marker.getSnippet());
                    return v;
                }

                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    return null;
                }
            });

            Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
            setMarkers();
        }
    };

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
        placesList = new ArrayList<com.example.eventiapp.model.Place>();
        placeEventsList = new ArrayList<>(); //EVENTI DI UN SINGOLO LUOGO
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(placeEventsList,
                requireActivity().getApplication(), 0,
                new EventsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onEventsItemClick(Events events) {
                        //VAI AI DETTAGLI DELL'EVENTO
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("event", events);
                        Navigation.findNavController(requireView()
                        ).navigate(R.id.action_mapsFragment_to_eventFragment, bundle);
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
        progressBar = view.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_events);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);


        eventsAndPlacesViewModel.getPlaces().observe(getViewLifecycleOwner(), result -> {

            if (result != null) {
                Log.i("SUCCESSO", "SUCCESSO");
                List<com.example.eventiapp.model.Place> fetchedPlaces = new ArrayList<>(result);

                if (!eventsAndPlacesViewModel.isLoading()) {
                    if (eventsAndPlacesViewModel.isFirstLoading()) {
                        eventsAndPlacesViewModel.setTotalResults(fetchedPlaces.size());
                        eventsAndPlacesViewModel.setFirstLoading(false);
                        this.placesList.addAll(fetchedPlaces);
                    } else {
                        // Updates related to the favorite status of the places
                        placesList.clear();
                        placesList.addAll(fetchedPlaces);
                    }
                } else {
                    eventsAndPlacesViewModel.setLoading(false);
                    eventsAndPlacesViewModel.setCurrentResults(placesList.size());
                    for (int i = 0; i < placesList.size(); i++) {
                        if (placesList.get(i) == null) {
                            placesList.remove(placesList.get(i));
                        }
                    }
                }
            } else {
                Log.i("FALLITO", "FALLITO");

                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(view, errorMessagesUtil.
                                getErrorMessage("ERRORE"),
                        Snackbar.LENGTH_SHORT).show();
            }
        });


        //BOTTOM SHEETS

        bottomSheet = view.findViewById(R.id.bottom_sheet1);
        tapactionlayout = view.findViewById(R.id.tap_action_layout);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

        scrollViewImagesPlace = view.findViewById(R.id.scrollViewImagesPlace);
        galleryPhotos = view.findViewById(R.id.galleryPhotos);
        carImageView = view.findViewById(R.id.carImageView);
        mapsImageView = view.findViewById(R.id.mapsImageView);
        callImageView = view.findViewById(R.id.callImageView);
        favoriteImageView = view.findViewById(R.id.favoriteImageView);
        placeTextView = view.findViewById(R.id.placeTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        numberOfEvents = view.findViewById(R.id.number_events_textView);

        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });


    }

    private void getEventsOfPlace(String id) {
        //EVENTI PRESENTI NEL LUOGO

        eventsAndPlacesViewModel.getPlaceEventsLiveData(id).observe(getViewLifecycleOwner(), result -> {

            if (result.isSuccess()) {
                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();

                if (!eventsAndPlacesViewModel.isLoading()) {
                    eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.placeEventsList.size());
                    this.placeEventsList.clear();
                    this.placeEventsList.addAll(fetchedEvents);
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                            this.placeEventsList.size());
                    progressBar.setVisibility(View.GONE);
                } else {
                    eventsAndPlacesViewModel.setLoading(false);
                    eventsAndPlacesViewModel.setCurrentResults(placeEventsList.size());

                    int initialSize = placeEventsList.size();

                    for (int i = 0; i < placeEventsList.size(); i++) {
                        if (placeEventsList.get(i) == null) {
                            placeEventsList.remove(placeEventsList.get(i));
                        }
                    }
                    int startIndex = (eventsAndPlacesViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                            EVENTS_PAGE_SIZE_VALUE;
                    for (int i = startIndex; i < fetchedEvents.size(); i++) {
                        placeEventsList.add(fetchedEvents.get(i));
                    }
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, placeEventsList.size());
                }
                numberOfEvents.setText(String.valueOf(placeEventsList.size()));
            } else {
                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(requireView(), errorMessagesUtil.
                                getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (myGoogleMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            myGoogleMap.clear();

            // add markers from database to the map
            setMarkers();
        }
    }

    private void setMarkers() {
        for (com.example.eventiapp.model.Place p : placesList) {
            double[] location = p.getCoordinates();
            LatLng latLng;
            if (location[0] > location[1]) {
                latLng = new LatLng(location[0], location[1]);
            } else {
                latLng = new LatLng(location[1], location[0]);
            }
            myGoogleMap.addMarker(new MarkerOptions().
                    position(latLng).
                    title(p.getName() + ":" + p.getId()).
                    snippet(p.getAddress()));
            myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                    LatLng position = marker.getPosition();
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(20).build();
                    myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    marker.showInfoWindow();

                    String[] parts = Objects.requireNonNull(marker.getTitle()).split(":");
                    String idPlace = parts[1];
                    getEventsOfPlace(idPlace);


                    //SETTA FOTO LUOGO

                    eventsAndPlacesViewModel.getSinglePlace(idPlace).observe(getViewLifecycleOwner(), result -> {
                        if (result != null) {
                            List<PhotoMetadata> images = result.getImages();
                            List<Bitmap> downloadedImages = new ArrayList<>();
                            AtomicInteger counter = new AtomicInteger(0);

                            PlaceDetailsSource.fetchPlacePhotos(images, false, new PlaceDetailsSource.PlacePhotosListener() {
                                @Override
                                public void onPlacePhotosListener(Bitmap bitmap) {
                                    scrollViewImagesPlace.setVisibility(View.VISIBLE);
                                    if (bitmap != null) {
                                        downloadedImages.add(bitmap);
                                        int count = counter.incrementAndGet();
                                        if (count == images.size()) {
                                            galleryPhotos.removeAllViews();
                                            for (Bitmap downloadedImage : downloadedImages) {
                                                View viewPlacePhoto = inflater.inflate(R.layout.item_place_photo_maps, galleryPhotos, false);
                                                ImageView imagePlace = viewPlacePhoto.findViewById(R.id.imagePlace);
                                                imagePlace.setImageBitmap(downloadedImage);
                                                galleryPhotos.addView(viewPlacePhoto);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    Log.i("ERROR", message);
                                }
                            });

                            placeTextView.setVisibility(View.VISIBLE);
                            placeTextView.setText(result.getName());
                            addressTextView.setText(result.getAddress());

                            carImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //NAVIGAZIONE
                                    String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", position.latitude, position.longitude);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                }
                            });

                            mapsImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //APRI SU GOOGLE MAPS
                                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", position.latitude, position.longitude);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                }
                            });

                            Place finalP = result;
                            callImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //CHIAMA POSTO

                                    String number = finalP.getPhoneNumber();
                                    if (number != null) {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(requireContext(), "NUMERO NON TROVATO", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            favoriteImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //AGGIUNGI POSTO AI FAVORITI DELL' UTENTE
                                }
                            });
                        }
                    });
                    return true;
                }
            });
        }
    }

}