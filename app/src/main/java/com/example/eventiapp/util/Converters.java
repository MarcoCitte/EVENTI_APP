package com.example.eventiapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.example.eventiapp.adapter.PhotoMetadataJsonAdapter;
import com.example.eventiapp.model.EventSource;
import com.example.eventiapp.model.Place;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converters {

    @TypeConverter
    public static List<Place> fromStringPlace(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Place>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static List<Double> fromStringDouble(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Double>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static List<String> fromStringString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromListOfPlace(List<Place> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromListOfDouble(List<Double> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromListOfString(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


    //PHOTOMETADATA OF PLACE
    @TypeConverter
    public static List<PhotoMetadata> fromStringImagesMetadata(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<PhotoMetadata>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PhotoMetadata.class, new PhotoMetadataJsonAdapter())
                .create();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromListOfImagesMetadata(List<PhotoMetadata> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromObjectToString(EventSource eventSource){
        Gson gson = new Gson();
        String json = gson.toJson(eventSource);
        return json;
    }

    @TypeConverter
    public static EventSource fromStringToObject(String value){
        Gson gson = new Gson();
        EventSource json = gson.fromJson(value,EventSource.class);
        return json;
    }


    //FOURSQUARE
    @TypeConverter
    public static String fromLocationToString(Location location){
        Gson gson = new Gson();
        String json = gson.toJson(location);
        return json;
    }

    @TypeConverter
    public static Location fromStringToLocation(String value){
        Gson gson = new Gson();
        Location json = gson.fromJson(value,Location.class);
        return json;
    }

}