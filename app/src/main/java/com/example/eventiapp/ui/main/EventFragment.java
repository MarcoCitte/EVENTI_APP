package com.example.eventiapp.ui.main;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentEventBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.ServiceLocator;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

public class EventFragment extends Fragment {

    private FragmentEventBinding fragmentEventBinding;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    MapView mMapView;
    private GoogleMap googleMap;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance() {
        return new EventFragment();
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

        if (events.getEventSource()!=null && events.getEventSource().getUrlPhoto() != null) {
            Glide.with(this).load(events.getEventSource().getUrlPhoto()).into(fragmentEventBinding.eventImage);
        }
        fragmentEventBinding.eventTitle.setText(events.getTitle());
        fragmentEventBinding.eventCategory.setText(events.getCategory());
        //fragmentEventBinding.eventTimezone.setText(events.getTimezone());
        fragmentEventBinding.eventDescription.setText(events.getDescription());
        if(events.getEnd()!=null) {
            String date = "FROM: " + events.getStart() + " TO: " + events.getEnd() + " (" + events.getTimezone() + ")";
            fragmentEventBinding.eventDate.setText(date);
        }else{
            String date=events.getStart() + " (" +events.getTimezone() +")";
            fragmentEventBinding.eventDate.setText(date);
        }
        if (!events.getPlaces().isEmpty()) {
            fragmentEventBinding.eventLocation.setText((CharSequence) events.getPlaces().get(0).getAddress());
        } else {
            fragmentEventBinding.eventLocation.setVisibility(View.GONE);
        }

        if(!events.getCategory().equals("movies")) { //PER ORA I MOVIES SON SOLO QUELLI DEL GIORNO CORRENTE
            eventsAndPlacesViewModel.getEventsDates(events.getTitle()).observe(getViewLifecycleOwner(), result -> {
                if (result.size() > 1) {
                    showAllEventsDate(result);
                }
            });
        }else{ //MOSTRA ORARI FILM DEL GIORNO CORRENTE
            //Log.i("NUMERO ROWS DB: ", String.valueOf(eventsAndPlacesViewModel.getCount()));
            eventsAndPlacesViewModel.getMoviesHours(events.getTitle()).observe(getViewLifecycleOwner(), result -> {
                if (result.length > 1) {
                    showAllHoursMovie(result);
                }
            });
        }


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
                double[] location = events.getCoordinates();
                LatLng latLng = new LatLng(location[1], location[0]);
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(events.getPlaces().get(0).getName()));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        LatLng position = marker.getPosition();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        googleMap.getMaxZoomLevel();

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

    private void showAllEventsDate(List<String> dates) {
        LinearLayout linearLayout = fragmentEventBinding.otherDatesLayout;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(20);
        TextView textView = fragmentEventBinding.otherDatesTextView;
        textView.setVisibility(View.VISIBLE);

        for (String date : dates) {
            MaterialButton button = new MaterialButton(requireContext());
            button.setLayoutParams(params);
            button.setTextSize(16);
            button.setPadding(15, 15, 15, 15);
            button.setCornerRadius(30);
            button.setText(date);
            button.setOnClickListener(v -> {
                //VA ALLO STESSO EVENTO MA CON DATA DIVERSA
            });
            linearLayout.addView(button);
        }
    }

    private void showAllHoursMovie(String[] hours){
        LinearLayout linearLayout = fragmentEventBinding.otherHoursLayout;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(20);
        TextView textView = fragmentEventBinding.otherHoursTextView;
        textView.setVisibility(View.VISIBLE);

        for (String hour : hours) {
            MaterialButton button = new MaterialButton(requireContext());
            button.setLayoutParams(params);
            button.setTextSize(16);
            button.setPadding(15, 15, 15, 15);
            button.setCornerRadius(30);
            button.setText(hour);
            button.setOnClickListener(v -> {
                //IN TEORIA NON DOVREBBE FAR NIENTE
            });
            linearLayout.addView(button);
        }
    }

}
