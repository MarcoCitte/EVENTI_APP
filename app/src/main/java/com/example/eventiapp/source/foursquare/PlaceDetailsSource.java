package com.example.eventiapp.source.foursquare;

import android.os.AsyncTask;

import com.example.eventiapp.model.Place;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaceDetailsSource extends AsyncTask<Void, Void, String> {

    private OkHttpClient client;
    private String name;
    private String address;
    private OnPlaceDetailsListener listener;


    public PlaceDetailsSource(String name, String address) {
        this.client = new OkHttpClient();
        this.name = name;
        this.address = address;
    }

    public interface OnPlaceDetailsListener {
        void onPlaceDetailsReceived(Place place);

        void onPlaceDetailsError();
    }

    public void setOnPlaceDetailsListener(OnPlaceDetailsListener listener) {
        this.listener = listener;
    }


    @Override
    protected String doInBackground(Void... voids) {
        String encodedName = null;
        String encodedAddress = null;
        String encodedCity = null;
        try {
            encodedName = URLEncoder.encode(name, "UTF-8");
            encodedAddress = URLEncoder.encode(address, "UTF-8");
            encodedCity = URLEncoder.encode("Milan", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://api.foursquare.com/v3/places/match?" +
                "name=" + encodedName +
                "&address=" + encodedAddress +
                "&city=" + encodedCity +
                "&cc=" + "IT";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "fsq3GFgP7uFX96oVogweG8dfk4vutAJXCgPQxSwwPnfTwMs=")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String jsonResponse) {
        if (jsonResponse != null) {
            parsePlaceDetails(jsonResponse);
        } else {

        }
    }

    public void parsePlaceDetails(String jsonResponse) {
        Gson gson = new Gson();
        PlaceDetailsResponse response = gson.fromJson(jsonResponse, PlaceDetailsResponse.class);
        if (response != null) {
            Place place = response.getPlace();
            if (place != null) {
                listener.onPlaceDetailsReceived(place);
            } else {
                listener.onPlaceDetailsError();
            }
        } else {
            listener.onPlaceDetailsError();
        }
    }


    class PlaceDetailsResponse {
        private Place place;
        private double match_score;

        public Place getPlace() {
            return place;
        }
    }


}
