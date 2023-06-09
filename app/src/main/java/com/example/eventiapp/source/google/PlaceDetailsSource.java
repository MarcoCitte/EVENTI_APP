package com.example.eventiapp.source.google;

import android.graphics.Bitmap;
import android.location.Geocoder;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Arrays;
import java.util.List;

public class PlaceDetailsSource {

    private static PlacesClient placesClient;
    private static Geocoder geoCoder;

    public PlaceDetailsSource(PlacesClient placesClient, Geocoder geoCoder) {
        PlaceDetailsSource.placesClient = placesClient;
        PlaceDetailsSource.geoCoder = geoCoder;
    }


    public interface PlaceDetailsListener {
        void onPlaceDetailsFetched(Place place);

        void onError(String message);
    }

    public interface PlacePhotosListener {
        void onPlacePhotosListener(Bitmap bitmap);

        void onError(String message);
    }

    public interface PlaceAutoCompleteListener {
        void onPlaceAutoCompleteListener(Place place);

        void onError(String message);
    }

    public void fetchPlaceDetails(String name, String address, PlaceDetailsListener listener) {

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Creiamo la stringa di ricerca per il posto unendo nome e indirizzo
        String query = name + " " + address;
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            if (!response.getAutocompletePredictions().isEmpty()) {
                // Prendiamo l'ID del primo posto nella lista delle predictions
                String placeId = response.getAutocompletePredictions().get(0).getPlaceId();

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.OPENING_HOURS,
                        Place.Field.TYPES, Place.Field.UTC_OFFSET, Place.Field.PHOTO_METADATAS);

                FetchPlaceRequest requestDetails = FetchPlaceRequest.builder(placeId, placeFields).build();

                placesClient.fetchPlace(requestDetails).addOnSuccessListener((response1) -> {
                    Place place = response1.getPlace();
                    listener.onPlaceDetailsFetched(place);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        String message = "Place not found: " + exception.getMessage();
                        listener.onError(message);
                    }
                });

            } else {
                listener.onError("Place not found");
            }
        }).addOnFailureListener((exception) -> {
            String message = "Place search failed: " + exception.getMessage();
            listener.onError(message);
        });
    }

    public static void fetchPlacePhotos(List<PhotoMetadata> metadata, boolean onePhoto, PlacePhotosListener listener) {

        if (true) {
            return; // PER NON FARE LA FETCH DELLE FOTO E RISPARMIARE IN TEST
        }

        if (metadata == null || metadata.isEmpty()) {
            Log.w("TAG", "No photo metadata.");
            return;
        }
        int length;
        if (onePhoto) {
            length = 1;
        } else {
            length = metadata.size();
        }
        for (int i = 0; i < length; i++) {
            if (metadata.get(i) != null) {
                PhotoMetadata photoMetadata = metadata.get(i);
                FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(1000) // Optional.
                        .setMaxHeight(1000) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    listener.onPlacePhotosListener(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        String message = "Place not found: " + exception.getMessage();
                        listener.onError(message);
                    }
                });
            }
        }
    }

    public static void fetchAutoCompletePlaces(AutocompleteSupportFragment autocompleteSupportFragment, PlaceAutoCompleteListener listener) {
        // Gestisci la selezione di un posto dall'autocompletamento
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                listener.onError(status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                listener.onPlaceAutoCompleteListener(place);
            }

        });
    }


}

