package com.example.eventiapp.ui.user;

import static android.app.Activity.RESULT_OK;
import static com.example.eventiapp.util.Constants.BICOCCA_LATLNG;
import static com.example.eventiapp.util.Constants.REQUEST_CODE_PICK_IMAGE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.eventiapp.databinding.FragmentAddPlaceBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class AddPlaceFragment extends Fragment {

    private FragmentAddPlaceBinding fragmentAddPlaceBinding;
    private GoogleMap googleMap;
    private MapView mapView;

    private Uri imageUri;
    private String name;
    private String address;
    private String city;
    private String cap;
    private String nation;
    private String phoneNumber;
    private LatLng latLng;


    public AddPlaceFragment() {
        // Required empty public constructor
    }

    public static AddPlaceFragment newInstance() {
        return new AddPlaceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddPlaceBinding = FragmentAddPlaceBinding.inflate(inflater, container, false);
        return fragmentAddPlaceBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = fragmentAddPlaceBinding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        if (mapView != null) {
            mapView.getMapAsync(callback);
        }

        latLng = BICOCCA_LATLNG; //DEFAULT BICOCCA


        //IMAGEVIEW

        fragmentAddPlaceBinding.placeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });

        //ADD BUTTON

        fragmentAddPlaceBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = String.valueOf(fragmentAddPlaceBinding.editTextName.getText());
                address = String.valueOf(fragmentAddPlaceBinding.editTextAddress.getText());
                city = String.valueOf(fragmentAddPlaceBinding.editTextCity.getText());
                cap = String.valueOf(fragmentAddPlaceBinding.editTextCap.getText());
                nation = String.valueOf(fragmentAddPlaceBinding.editTextNation.getText());
                phoneNumber = String.valueOf(fragmentAddPlaceBinding.editTextPhoneNumber.getText()); //FACOLTATIVO

                boolean isOk = true;
                if (name == null || name.isEmpty()) {
                    fragmentAddPlaceBinding.editTextName.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (address == null || address.isEmpty()) {
                    fragmentAddPlaceBinding.editTextAddress.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (city == null || city.isEmpty()) {
                    fragmentAddPlaceBinding.editTextCity.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (cap == null || cap.isEmpty()) {
                    fragmentAddPlaceBinding.editTextCap.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (nation == null || nation.isEmpty()) {
                    fragmentAddPlaceBinding.editTextNation.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }


                if (isOk) {
                    //AGGIUNGI POSTO

                    Place place = new Place();
                    place.setId(name + address + latLng.toString()); //?
                    place.setName(name);
                    place.setAddress(address + ", " + city + ", " + cap + ", " + nation);
                    List<Double> coordinates = new ArrayList<>();
                    coordinates.add(latLng.latitude);
                    coordinates.add(latLng.latitude);
                    place.setCoordinates(coordinates);
                    place.setPhoneNumber(phoneNumber);

                    Navigation.findNavController(requireView()).navigate(R.id.action_addPlaceFragment_to_containerMyEventsAndPlaces);
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            (getString(R.string.place_added)), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        //DELETE BUTTON

        fragmentAddPlaceBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_addPlaceFragment_to_containerMyEventsAndPlaces);
            }
        });
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        public void onMapReady(@NonNull GoogleMap mMap) {
            googleMap = mMap;
            googleMap.addMarker(new MarkerOptions().position(BICOCCA_LATLNG).draggable(true));
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    latLng = marker.getPosition();
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });

            CameraPosition cameraPosition = new CameraPosition.Builder().target(BICOCCA_LATLNG).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri;
                    fragmentAddPlaceBinding.placeImage.setImageURI(imageUri);
                }
            }
        }
    }


}