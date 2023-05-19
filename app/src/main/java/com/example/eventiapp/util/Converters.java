package com.example.eventiapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public static String[] fromString(String value) {
        Type listType = new TypeToken<String[]>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(String[] list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String JSONArrayfromDoubleArray(double[] values) {
        JSONArray jsonArray = new JSONArray();
        for (double value : values) {
            try {
                jsonArray.put(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    @TypeConverter
    public static double[] JSONArrayToDoubleArray(String values) {
        try {
            JSONArray jsonArray = new JSONArray(values);
            double[] doubleArray = new double[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                doubleArray[i] = Double.parseDouble(jsonArray.getString(i));
            }
            return doubleArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

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
    public static String fromListOfPlace(List<Place> list) {
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
}