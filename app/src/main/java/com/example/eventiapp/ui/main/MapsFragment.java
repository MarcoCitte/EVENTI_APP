package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Point;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsApiResponse;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment {

    private EventsViewModel eventsViewModel;
    private List<Events> eventsList;
    private Marker marker;
    private UiSettings mUiSettings;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

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

            for (int i = 0; i < placesList.size(); i++) {
                count++;
                double[] location = placesList.get(i).getCoordinates();
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(location[1], location[0])).title(placesList.get(i).getPlaces().get(0).getName()));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        LatLng position = marker.getPosition();
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(20).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
    }
}