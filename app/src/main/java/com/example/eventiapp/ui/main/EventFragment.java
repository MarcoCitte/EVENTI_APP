package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.EVENTS_PAGE_SIZE_VALUE;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
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
import com.example.eventiapp.databinding.FragmentEventBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventFragment extends Fragment {

    private static final String TAG = EventFragment.class.getSimpleName();

    private FragmentEventBinding fragmentEventBinding;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private EventsRecyclerViewAdapter eventsRecyclerViewAdapter;
    private List<Events> eventsList; //EVENTI SIMILI

    private int totalItemCount; // Total number of events
    private int lastVisibleItem; // The position of the last visible event item
    private int visibleItemCount; // Number or total visible event items

    // Based on this value, the process of loading more events is anticipated or postponed
    private final int threshold = 1;

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
        eventsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEventBinding = FragmentEventBinding.inflate(inflater, container, false);
        return fragmentEventBinding.getRoot();
    }

    @SuppressLint("SimpleDateFormat")
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

        mMapView = fragmentEventBinding.mapView;
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


        assert getArguments() != null;
        Events events = getArguments().getParcelable("event");
        setImageViewFavoriteEvent(events.isFavorite());

        fragmentEventBinding.imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareEvent(requireContext(), events);
            }
        });

        fragmentEventBinding.imageViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.addToCalendar(requireContext(), events);
            }
        });

        fragmentEventBinding.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageViewFavoriteEvent(!events.isFavorite());
                events.setFavorite(!events.isFavorite());
                eventsAndPlacesViewModel.updateEvents(events);            }
        });


        if (!events.getPlaces().isEmpty() && events.getPlaces().get(0).getName() != null) {
            eventsAndPlacesViewModel.getSinglePlace(events.getPlaces().get(0).getId()).observe(getViewLifecycleOwner(), result -> {
                if (result != null) { //PRENDE I DETTAGLI DEL POSTO PRESI DA GOOGLE
                    if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
                        fragmentEventBinding.eventImage.setVisibility(View.VISIBLE);
                        Glide.with(this).load(events.getEventSource().getUrlPhoto()).into(fragmentEventBinding.eventImage);
                    } else {
                        fragmentEventBinding.eventImage.setVisibility(View.VISIBLE);
                        if (result.getImages() != null && !result.getImages().isEmpty()) {
                            PlaceDetailsSource.fetchPlacePhotos(result.getImages(), true, new PlaceDetailsSource.PlacePhotosListener() {
                                @Override
                                public void onPlacePhotosListener(Bitmap bitmap) {
                                    if (bitmap != null) {
                                        Glide.with(requireView()).load(bitmap).into(fragmentEventBinding.eventImage);
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    Log.e(TAG, message);
                                    fragmentEventBinding.eventImage.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            fragmentEventBinding.eventImage.setVisibility(View.GONE);
                        }
                    }
                    fragmentEventBinding.eventTitle.setText(events.getTitle());
                    fragmentEventBinding.eventCategory.setText(events.getCategory());
                    if (events.getEventSource() != null) {
                        fragmentEventBinding.sourceTV.setText(events.getEventSource().getUrl());
                    } else {
                        fragmentEventBinding.forMoreInfoTV.setVisibility(View.GONE);
                        fragmentEventBinding.sourceTV.setVisibility(View.GONE);
                    }

                    //CATEGORY

                    fragmentEventBinding.eventCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle=new Bundle();
                            bundle.putString("category",fragmentEventBinding.eventCategory.getText().toString());
                            Navigation.findNavController(requireView()).navigate(R.id.action_eventFragment_to_categoryFragment, bundle);
                        }
                    });

                    if (events.getDescription() != null && !Objects.equals(events.getDescription(), "")) {
                        fragmentEventBinding.eventDescription.setText(events.getDescription());
                    } else {
                        fragmentEventBinding.showInfoTV.setVisibility(View.GONE);
                        fragmentEventBinding.eventDescription.setVisibility(View.GONE);
                    }

                    SimpleDateFormat outputFormat;
                    if (events.getEventSource() == null) {  //QUESTI EVENTI HANNO ANCHE L'ORARIO
                        outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                    } else {
                        outputFormat = new SimpleDateFormat("dd MMM yyyy");
                    }

                    if (events.getEnd() != null && !Objects.equals(events.getStart(), events.getEnd())) {
                        String dateStart = events.getStart();
                        String dateEnd = events.getEnd();
                        Date date1 = DateUtils.parseDateToShow(dateStart, "EN");
                        Date date2 = DateUtils.parseDateToShow(dateEnd, "EN");
                        String formattedDate = outputFormat.format(Objects.requireNonNull(date1)) + " \n " + outputFormat.format(Objects.requireNonNull(date2));
                        fragmentEventBinding.eventDate.setText(formattedDate);
                    } else {
                        String date = events.getStart();
                        Date date1 = DateUtils.parseDateToShow(date, "EN");
                        String formattedDate = outputFormat.format(Objects.requireNonNull(date1));
                        fragmentEventBinding.eventDate.setText(formattedDate);
                    }

                    fragmentEventBinding.eventPlace.setText(result.getName());
                    fragmentEventBinding.eventAddress.setText(result.getAddress());
                    fragmentEventBinding.eventAddress2.setText(result.getAddress());

                    if (result.getPhoneNumber() != null) {
                        fragmentEventBinding.phoneNumber.setText(result.getPhoneNumber());
                        fragmentEventBinding.phoneNumber.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + result.getPhoneNumber()));
                                startActivity(intent);
                            }
                        });
                    } else {
                        fragmentEventBinding.phoneNumber.setVisibility(View.GONE);
                    }

                    if (!events.getCategory().equals("movies")) { //PER ORA I MOVIES SON SOLO QUELLI DEL GIORNO CORRENTE
                        eventsAndPlacesViewModel.getEventsDates(events.getTitle()).observe(getViewLifecycleOwner(), result2 -> {
                            if (result2.size() > 1) {
                                showAllEventsDate(result2);
                            }
                        });
                    } else { //MOSTRA ORARI FILM DEL GIORNO CORRENTE
                        showAllHoursMovie(events.getHours());
                    }

                    //EVENTI SIMILI ---------------------------------------------------------------------

                    sameCategoryEvents(events);


                    //GOOGLE MAPS ---------------------------------------------------------------------------------------------------

                    if (!fragmentEventBinding.eventPlace.getText().equals("Unknown")) {
                        LatLng latLng;
                        if (result.getCoordinates().get(0) > result.getCoordinates().get(1)) {
                            latLng = new LatLng(result.getCoordinates().get(0), result.getCoordinates().get(1));
                        } else {
                            latLng = new LatLng(result.getCoordinates().get(1), result.getCoordinates().get(0));
                        }

                        googleMaps(latLng, result.getName());
                    } else {
                        fragmentEventBinding.mapView.setVisibility(View.GONE);
                    }

                } else {  //NON ESISTE IL PLACE DELL'EVENTO NEL DATABASE
                    showEventWithNoPlace(events);
                }
            });
        } else { //L'evento non ha il nome del place in cui si tiene
            showEventWithNoPlace(events);
        }

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

    private void showAllHoursMovie(List<String> hours) {
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

    private void sameCategoryEvents(Events events) {

        RecyclerView recyclerView = fragmentEventBinding.recyclerviewEvents;
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
                        Navigation.findNavController(requireView()).navigate(R.id.action_eventFragment_self, bundle);
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
                        eventsList.get(position).setFavorite(!eventsList.get(position).isFavorite());
                        eventsAndPlacesViewModel.updateEvents(eventsList.get(position));
                    }

                    @Override
                    public void onModeEventButtonPressed(Events events) {

                    }

                    @Override
                    public void onDeleteEventButtonPressed(Events events) {

                    }
                });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(eventsRecyclerViewAdapter);

        fragmentEventBinding.progressBar.setVisibility(View.VISIBLE);

        eventsAndPlacesViewModel.getCategoryEventsLiveData(events.getCategory()).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    Log.i("SUCCESSO", "SUCCESSO");
                    EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                    List<Events> fetchedEvents = eventsResponse.getEventsList();
                    fetchedEvents.remove(events); //RIMUOVI LO STESSO EVENTO

                    if (fetchedEvents.size() > 0) {
                        fragmentEventBinding.categoryEventsTV.setVisibility(View.VISIBLE);
                        fragmentEventBinding.line.setVisibility(View.VISIBLE);
                        fragmentEventBinding.recyclerviewEvents.setVisibility(View.VISIBLE);
                        if (!eventsAndPlacesViewModel.isLoading()) {
                            eventsRecyclerViewAdapter.notifyItemRangeRemoved(0, this.eventsList.size());
                            this.eventsList.clear();
                            this.eventsList.addAll(fetchedEvents);
                            eventsRecyclerViewAdapter.notifyItemChanged(0, fetchedEvents.size());
                            fragmentEventBinding.progressBar.setVisibility(View.GONE);

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
                        fragmentEventBinding.progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Log.i("FALLITO", "FALLITO");

                    ErrorMessageUtil errorMessagesUtil =
                            new ErrorMessageUtil(requireActivity().getApplication());
                    Snackbar.make(requireView(), errorMessagesUtil.
                                    getErrorMessage(((Result.Error) result).getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                    fragmentEventBinding.progressBar.setVisibility(View.GONE);
                }

            } else {
                //NON CI SONO EVENTI SIMILI
                fragmentEventBinding.categoryEventsTV.setVisibility(View.GONE);
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
                                    eventsAndPlacesViewModel.getCategoryEventsLiveData(events.getCategory()).getValue() != null &&
                                    eventsAndPlacesViewModel.getCurrentResults() != eventsAndPlacesViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> eventsListMutableLiveData = eventsAndPlacesViewModel.getCategoryEventsLiveData(events.getCategory());

                        if (eventsListMutableLiveData.getValue() != null &&
                                eventsListMutableLiveData.getValue().isSuccess()) {

                            eventsAndPlacesViewModel.setLoading(true);
                            eventsList.add(null);
                            eventsRecyclerViewAdapter.notifyItemRangeInserted(eventsList.size(),
                                    eventsList.size() + 1);

                            int page = eventsAndPlacesViewModel.getPage() + 1;
                            eventsAndPlacesViewModel.setPage(page);
                            eventsAndPlacesViewModel.getCategoryEventsLiveData(events.getCategory());
                        }
                    }
                }
            }
        });
    }


    private void googleMaps(LatLng latLng, String placeName) {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap mMap) {
                googleMap = mMap;
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
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
        fragmentEventBinding = null;
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

    @SuppressLint("SimpleDateFormat")
    private void showEventWithNoPlace(Events events) {
        //L'evento non ha il nome del place in cui si tiene

        if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
            fragmentEventBinding.eventImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(events.getEventSource().getUrlPhoto()).into(fragmentEventBinding.eventImage);
        } else {
            fragmentEventBinding.eventImage.setVisibility(View.GONE);
        }

        fragmentEventBinding.eventTitle.setText(events.getTitle());
        fragmentEventBinding.eventCategory.setText(events.getCategory());

        //CATEGORY

        fragmentEventBinding.eventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category",fragmentEventBinding.eventCategory.getText().toString());
                Navigation.findNavController(requireView()).navigate(R.id.action_eventFragment_to_categoryFragment, bundle);
            }
        });

        if (events.getEventSource() != null) {
            fragmentEventBinding.sourceTV.setText(events.getEventSource().getUrl());
        } else {
            fragmentEventBinding.forMoreInfoTV.setVisibility(View.GONE);
            fragmentEventBinding.sourceTV.setVisibility(View.GONE);
        }

        if (events.getDescription() != null && !Objects.equals(events.getDescription(), "")) {
            fragmentEventBinding.eventDescription.setText(events.getDescription());
        } else {
            fragmentEventBinding.showInfoTV.setVisibility(View.GONE);
            fragmentEventBinding.eventDescription.setVisibility(View.GONE);
        }

        SimpleDateFormat outputFormat;
        if (events.getEventSource() == null) {  //QUESTI EVENTI HANNO ANCHE L'ORARIO
            outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        } else {
            outputFormat = new SimpleDateFormat("dd MMM yyyy");
        }

        if (events.getEnd() != null && !Objects.equals(events.getStart(), events.getEnd())) {
            String dateStart = events.getStart();
            String dateEnd = events.getEnd();
            Date date1 = DateUtils.parseDateToShow(dateStart, "EN");
            Date date2 = DateUtils.parseDateToShow(dateEnd, "EN");
            String formattedDate = outputFormat.format(Objects.requireNonNull(date1)) + "\n" + outputFormat.format(Objects.requireNonNull(date2));
            fragmentEventBinding.eventDate.setText(formattedDate);
        } else {
            String date = events.getStart();
            Date date1 = DateUtils.parseDateToShow(date, "EN");
            String formattedDate = outputFormat.format(Objects.requireNonNull(date1));
            fragmentEventBinding.eventDate.setText(formattedDate);
        }

        if (!events.getPlaces().isEmpty()) {
            if (events.getPlaces().get(0).getName() != null) {
                fragmentEventBinding.eventPlace.setText(events.getPlaces().get(0).getName());
                if (events.getPlaces().get(0).getAddress() != null) {
                    fragmentEventBinding.eventAddress.setText(events.getPlaces().get(0).getAddress());
                    fragmentEventBinding.eventAddress2.setText(events.getPlaces().get(0).getAddress());
                } else {
                    fragmentEventBinding.eventAddress.setVisibility(View.GONE);
                    fragmentEventBinding.eventAddress2.setVisibility(View.GONE);
                }
            } else {
                fragmentEventBinding.eventPlace.setText(R.string.unknown);
                fragmentEventBinding.eventAddress.setVisibility(View.GONE);
                fragmentEventBinding.eventAddress2.setVisibility(View.GONE);
            }
        } else {
            fragmentEventBinding.eventPlace.setText(R.string.unknown);
            fragmentEventBinding.eventAddress.setVisibility(View.GONE);
            fragmentEventBinding.eventAddress2.setVisibility(View.GONE);
        }

        fragmentEventBinding.phoneNumber.setVisibility(View.GONE);

        if (!events.getCategory().equals("movies")) { //PER ORA I MOVIES SON SOLO QUELLI DEL GIORNO CORRENTE
            eventsAndPlacesViewModel.getEventsDates(events.getTitle()).observe(getViewLifecycleOwner(), result2 -> {
                if (result2.size() > 1) {
                    showAllEventsDate(result2);
                }
            });
        } else { //MOSTRA ORARI FILM DEL GIORNO CORRENTE
            showAllHoursMovie(events.getHours());
        }

        //EVENTI SIMILI ---------------------------------------------------------------------

        sameCategoryEvents(events);


        //GOOGLE MAPS ---------------------------------------------------------------------------------------------------
        if (!fragmentEventBinding.eventPlace.getText().equals(R.string.unknown)) {
            if (events.getCoordinates().get(0) > events.getCoordinates().get(1)) { //SONO GIUSTE
                googleMaps(new LatLng(events.getCoordinates().get(0), events.getCoordinates().get(1)), null);
            } else {
                googleMaps(new LatLng(events.getCoordinates().get(1), events.getCoordinates().get(0)), null);
            }
        } else {
            fragmentEventBinding.mapView.setVisibility(View.GONE);
        }
    }

    private void setImageViewFavoriteEvent(boolean isFavorite) {
        if (isFavorite) {
            fragmentEventBinding.imageViewFavorite.setImageDrawable(
                    AppCompatResources.getDrawable(getContext(),
                            R.drawable.ic_baseline_favorite_24));
        } else {
            fragmentEventBinding.imageViewFavorite.setImageDrawable(
                    AppCompatResources.getDrawable(getContext(),
                            R.drawable.ic_baseline_favorite_border_24));
        }
    }



}
