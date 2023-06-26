package com.example.eventiapp.ui.user;

import static android.app.Activity.RESULT_OK;
import static com.example.eventiapp.util.Constants.BICOCCA_LATLNG;
import static com.example.eventiapp.util.Constants.REQUEST_CODE_PICK_IMAGE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAddPlaceBinding;
import com.example.eventiapp.databinding.FragmentEditPlaceBinding;
import com.example.eventiapp.model.Events;
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


public class EditPlaceFragment extends Fragment {

    private FragmentEditPlaceBinding fragmentEditPlaceBinding;
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


    public EditPlaceFragment() {}

    public static EditPlaceFragment newInstance() {
        return new EditPlaceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditPlaceBinding = FragmentEditPlaceBinding.inflate(inflater, container, false);
        return fragmentEditPlaceBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BUNDLE
        assert getArguments() != null;
        Place place = getArguments().getParcelable("place");

        mapView = fragmentEditPlaceBinding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        if (mapView != null) {
            mapView.getMapAsync(callback);
        }


        //IMAGEVIEW

        fragmentEditPlaceBinding.placeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });

        //COMPILA I CAMPI IN BASE AL PLACE CHE VUOLE MODIFICARE
        String[] partsAddress = place.getAddress().split(", ");
        fragmentEditPlaceBinding.editTextName.setText(place.getName());
        fragmentEditPlaceBinding.editTextAddress.setText(partsAddress[0]);
        fragmentEditPlaceBinding.editTextCity.setText(partsAddress[1]);
        fragmentEditPlaceBinding.editTextCap.setText(partsAddress[2]);
        fragmentEditPlaceBinding.editTextNation.setText(partsAddress[3]);
        if (place.getPhoneNumber() != null) {
            fragmentEditPlaceBinding.editTextPhoneNumber.setText(place.getPhoneNumber());
        }
        latLng = new LatLng(place.getCoordinates().get(0), place.getCoordinates().get(1));

        //ADD BUTTON

        fragmentEditPlaceBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = String.valueOf(fragmentEditPlaceBinding.editTextName.getText());
                address = String.valueOf(fragmentEditPlaceBinding.editTextAddress.getText());
                city = String.valueOf(fragmentEditPlaceBinding.editTextCity.getText());
                cap = String.valueOf(fragmentEditPlaceBinding.editTextCap.getText());
                nation = String.valueOf(fragmentEditPlaceBinding.editTextNation.getText());
                phoneNumber = String.valueOf(fragmentEditPlaceBinding.editTextPhoneNumber.getText()); //FACOLTATIVO

                boolean isOk = true;
                if (name == null || name.isEmpty()) {
                    fragmentEditPlaceBinding.editTextName.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (address == null || address.isEmpty()) {
                    fragmentEditPlaceBinding.editTextAddress.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (city == null || city.isEmpty()) {
                    fragmentEditPlaceBinding.editTextCity.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (cap == null || cap.isEmpty()) {
                    fragmentEditPlaceBinding.editTextCap.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (nation == null || nation.isEmpty()) {
                    fragmentEditPlaceBinding.editTextNation.setError(getString(R.string.field_mandatory));
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

        //CANCEL BUTTON
        fragmentEditPlaceBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_addPlaceFragment_to_containerMyEventsAndPlaces);
            }
        });

        //DELETE PLACE
        fragmentEditPlaceBinding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        public void onMapReady(@NonNull GoogleMap mMap) {
            googleMap = mMap;
            googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
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

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
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
                    fragmentEditPlaceBinding.placeImage.setImageURI(imageUri);
                }
            }
        }
    }
}