package com.example.eventiapp.util;

import android.content.Context;
import android.content.Intent;

import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;

public class ShareUtils {

    public static void shareEvent(Context context, Events events){
        String title = events.getTitle();
        String description = events.getDescription();
        String date = events.getStart();
        String location=events.getPlaces().get(0).getName();
        String address = events.getPlaces().get(0).getAddress();

        String shareText = "Check out this event:\n" +
                "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Date: " + date + "\n" +
                "Location" + location +"\n" +
                "Address: " + address;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Event");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, "Share Event"));
    }


    public static void sharePlace(Context context, Place place){
        String name = place.getName();
        String address = place.getAddress();

        String shareText = "Check out this place:\n" +
                "Location: " + name + "\n" +
                "Address: " + address;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Place");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, "Share Place"));
    }
}
