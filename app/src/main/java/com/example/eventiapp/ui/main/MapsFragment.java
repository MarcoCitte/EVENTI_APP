package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;
import static com.example.eventiapp.util.Constants.EVENTS_VIEW_TYPE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsRecyclerViewAdapter;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapsFragment extends Fragment {

    private EventsViewModel eventsViewModel;
    private List<Events> eventsList;
    private List<Events> placeEventsList;
    private Marker marker;
    private UiSettings mUiSettings;
    private Geocoder geoCoder;
    private PlacesClient placesClient;
    private BottomSheetBehavior mBottomSheetBehavior1;
    View bottomSheet;
    LinearLayout tapactionlayout;
    private ImageView placeImageView;
    private ImageView carImageView;
    private ImageView mapsImageView;
    private ImageView callImageView;
    private ImageView favoriteImageView;

    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items
    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mUiSettings = googleMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setMapToolbarEnabled(true);


            LatLng bicocca = new LatLng(45.51851, 9.2075123);
           /*
            googleMap.addMarker(new MarkerOptions().position(bicocca).title("Marker in Bicocca"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(bicocca));
            */

            float zoomLevel = 15.0f; //This goes up to 21
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bicocca, zoomLevel));

            int count = 0;

            //RIMUOVE LUOGHI DUPLICATI E SENZA COORDINATE COSI DA NON AVERE COORDINATE UGUALI
            Map<String, Events> map = new HashMap<String, Events>();
            for (Events e : eventsList) {
                if (!e.getPlaces().isEmpty() && !e.getCategory().equals("severe-weather") && !e.getCategory().equals("airport-delays")) {
                    String idPlace = e.getPlaces().get(0).getId();
                    if (!map.containsKey(idPlace)) {
                        map.put(idPlace, e);
                    }
                }
            }

            List<Events> placesList = new ArrayList<>(map.values());
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Nullable
                @Override
                public View getInfoContents(@NonNull Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window_maps, null);
                    TextView textViewName = v.findViewById(R.id.place_name);
                    TextView textViewAddress = v.findViewById(R.id.place_address);

                    String[] parts = Objects.requireNonNull(marker.getTitle()).split(":");
                    String namePlace = parts[0];
                    String idPlace = parts[1];
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

            geoCoder = new Geocoder(getContext(), Locale.getDefault());

            for (int i = 0; i < placesList.size(); i++) {
                count++;
                LatLng latLng = getCoordinates(placesList.get(i).getPlaces().get(0).getName());
                if (latLng != null) {
                    //GEOCODER HA TROVATO LE COORDINATE DEL LUOGO
                } else {
                    //USA COORDINATE ESTRATTE DALLE API
                    double[] location = placesList.get(i).getCoordinates();
                    latLng = new LatLng(location[1], location[0]);
                }
                marker = googleMap.addMarker(new MarkerOptions().
                        position(latLng).
                        title(placesList.get(i).getPlaces().get(0).getName() + ":" + placesList.get(i).getPlaces().get(0).getId()).
                        snippet(getAddress(placesList.get(i).getPlaces().get(0).getName())));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        tapactionlayout.setVisibility(View.VISIBLE);
                        LatLng position = marker.getPosition();
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(20).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        marker.showInfoWindow();

                        String[] parts = Objects.requireNonNull(marker.getTitle()).split(":");
                        String idPlace = parts[1];
                        getEventsOfPlace(idPlace);

                        //SETTA FOTO LUOGO

                        List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
                        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(idPlace, fields);

                        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                            Place place = response.getPlace();

                            // Get the photo metadata.
                            List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                            if (metadata == null || metadata.isEmpty()) {
                                Log.w("TAG", "No photo metadata.");
                                return;
                            }
                            PhotoMetadata photoMetadata = metadata.get(0);

                            // Get the attribution text.
                            String attributions = photoMetadata.getAttributions();

                            // Create a FetchPhotoRequest.
                            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(1000) // Optional.
                                    .setMaxHeight(1000) // Optional.
                                    .build();
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                placeImageView.setImageBitmap(bitmap);
                            }).addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e("ERROR", "Place not found: " + exception.getMessage());
                                    int statusCode = apiException.getStatusCode();
                                }
                            });
                        });


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

                        callImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //CHIAMA POSTO

                                List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER);
                                final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance("ChIJ3QKsnCXHhkcR3pA-eU_d9io", placeFields);
                                placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                                    final Place place = response.getPlace();
                                    Log.i("TAG", "Phone found: " + place.getPhoneNumber());
                                    String number = place.getPhoneNumber();
                                    if (number != null) {
                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(requireContext(), "NUMERO NON TROVATO", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                        favoriteImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //AGGIUNGI POSTO AI FAVORITI DELL' UTENTE
                            }
                        });
                        return true;
                    }
                });
            }


            Log.i("NUMERO COORDINATE: ", count + " ");
        }
    };

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
        eventsList = new ArrayList<>();
        placeEventsList = new ArrayList<>();
        Places.initialize(getContext(), "AIzaSyBfUbHrX9y475T-c7v--HuxDmxjUMldAE8");
        placesClient = Places.createClient(getContext());
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recyclerview_events);


        String country = "IT"; //POI VERRA PRESA DALLE SHAREDPREFERENCES
        String location = "45.51851, 9.2075123"; //BICOCCA
        double radius = 4.2;
        String sort = "start";
        String date = AllEventsFragment.currentDate();
        int limit = 5000;
        String lastUpdate = "0";


        eventsViewModel.getEvents(country, radius + "km@" + location, date, sort, limit, Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {

            if (result.isSuccess()) {
                Log.i("SUCCESSO", "SUCCESSO IN MAPS FRAGMENT");

                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();
                Log.i("COUNT RESULTS: ", ((EventsApiResponse) eventsResponse).getCount() + " ");
                Log.i("FETCHED EVENTS", fetchedEvents.toString());

                if (!eventsViewModel.isLoading()) {

                    eventsViewModel.setTotalResults(((EventsApiResponse) eventsResponse).getCount());
                    eventsViewModel.setFirstLoading(false);
                    this.eventsList.addAll(fetchedEvents);

                } else {
                    eventsViewModel.setLoading(false);
                    eventsViewModel.setCurrentResults(eventsList.size());

                    int initialSize = eventsList.size();

                    for (int i = 0; i < eventsList.size(); i++) {
                        if (eventsList.get(i) == null) {
                            eventsList.remove(eventsList.get(i));
                        }
                    }
                    int startIndex = (eventsViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                            EVENTS_PAGE_SIZE_VALUE;
                    for (int i = startIndex; i < fetchedEvents.size(); i++) {
                        eventsList.add(fetchedEvents.get(i));
                    }
                }
            } else {
                Log.i("FALLITO", "FALLITO");

                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(view, errorMessagesUtil.
                                getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
            }
        });


        //BOTTOM SHEETS

        bottomSheet = view.findViewById(R.id.bottom_sheet1);
        tapactionlayout = (LinearLayout) view.findViewById(R.id.tap_action_layout);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

        placeImageView = view.findViewById(R.id.placeImageView);
        carImageView = view.findViewById(R.id.carImageView);
        mapsImageView = view.findViewById(R.id.mapsImageView);
        callImageView = view.findViewById(R.id.callImageView);
        favoriteImageView = view.findViewById(R.id.favoriteImageView);


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
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false);

        eventsRecyclerViewAdapter = new EventsRecyclerViewAdapter(placeEventsList,
                requireActivity().getApplication(), EVENTS_VIEW_TYPE,
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
                    public void onPlacesItemClick(Events events) {

                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        //SETTA EVENTO COME PREFERITO
                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);


        eventsViewModel.getPlaceEventsLiveData(id).observe(getViewLifecycleOwner(), result -> {

            if (result.isSuccess()) {
                Log.i("SUCCESSO", "SUCCESSO");

                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();

                if (!eventsViewModel.isLoading()) {
                    this.placeEventsList.addAll(fetchedEvents);
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(0,
                            this.placeEventsList.size());
                    progressBar.setVisibility(View.GONE);
                } else {
                    eventsViewModel.setLoading(false);
                    eventsViewModel.setCurrentResults(placeEventsList.size());

                    int initialSize = placeEventsList.size();

                    for (int i = 0; i < placeEventsList.size(); i++) {
                        if (placeEventsList.get(i) == null) {
                            placeEventsList.remove(placeEventsList.get(i));
                        }
                    }
                    int startIndex = (eventsViewModel.getPage() * EVENTS_PAGE_SIZE_VALUE) -
                            EVENTS_PAGE_SIZE_VALUE;
                    for (int i = startIndex; i < fetchedEvents.size(); i++) {
                        placeEventsList.add(fetchedEvents.get(i));
                    }
                    eventsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, placeEventsList.size());
                }
            } else {
                Log.i("FALLITO", "FALLITO");

                ErrorMessageUtil errorMessagesUtil =
                        new ErrorMessageUtil(requireActivity().getApplication());
                Snackbar.make(requireView(), errorMessagesUtil.
                                getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();

                if (isConnected && totalItemCount != eventsViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !eventsViewModel.isLoading()
                            ) &&
                                    eventsViewModel.getPlaceEventsLiveData(id).getValue() != null &&
                                    eventsViewModel.getCurrentResults() != eventsViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> eventsListMutableLiveData = eventsViewModel.getPlaceEventsLiveData(id);

                        if (eventsListMutableLiveData.getValue() != null &&
                                eventsListMutableLiveData.getValue().isSuccess()) {

                            eventsViewModel.setLoading(true);
                            placeEventsList.add(null);
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(placeEventsList.size(),
                                    placeEventsList.size() + 1);

                            int page = eventsViewModel.getPage() + 1;
                            eventsViewModel.setPage(page);
                            eventsViewModel.getPlaceEventsLiveData(id);
                        }
                    }
                }
            }
        });
    }

    private LatLng getCoordinates(String name) {
        try {
            List<Address> addresses = geoCoder.getFromLocationName(name + "Milan", 1);
            if (addresses.size() > 0) {
                double lat = (double) (addresses.get(0).getLatitude());
                double lon = (double) (addresses.get(0).getLongitude());
                LatLng latLng = new LatLng(lat, lon);
                return latLng;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getAddress(String name) {
        try {
            List<Address> addresses = geoCoder.getFromLocationName(name + "Milan", 1);
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                return address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getPhone(String idPlace) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER);
        final String[] number = new String[1];
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(idPlace, placeFields);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();
            Log.i("TAG", "Phone found: " + place.getPhoneNumber());
            number[0] = place.getPhoneNumber();
        });
        return number[0];
    }

    private Bitmap getPhotos(String idPlace) {
        //FOTO LUOGO
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(idPlace, fields);
        final Bitmap[] bitmap = new Bitmap[1];

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("TAG", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                bitmap[0] = fetchPhotoResponse.getBitmap();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("ERROR", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                }
            });
        });
        return bitmap[0];
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


}