package com.example.eventiapp.util;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShareUtils {

    public static void shareEvent(Context context, Events events){
        String title = events.getTitle();
        String description = events.getDescription();
        String date = events.getStart();
        String location=events.getPlaces().get(0).getName();
        String address = events.getPlaces().get(0).getAddress();

        String shareText = context.getString(R.string.check_out_this_event) +
                context.getString(R.string.title_share) + title + "\n" +
                context.getString(R.string.description_share) + description + "\n" +
                context.getString(R.string.date_share) + date + "\n" +
                context.getString(R.string.location_share) + location +"\n" +
                context.getString(R.string.address_share) + address;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Event");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_event)));
    }


    public static void sharePlace(Context context, Place place){
        String name = place.getName();
        String address = place.getAddress();

        String shareText = context.getString(R.string.check_out_this_place) +
                context.getString(R.string.location_share) + name + "\n" +
                context.getString(R.string.address_share) + address;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Place");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_place)));
    }

    public static void addToCalendar(Context context, Events events){
        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.putExtra(CalendarContract.Events.TITLE, events.getTitle());
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, events.getPlaces().get(0).getName() + "( " + events.getPlaces().get(0).getAddress() + ")");
        if (events.getDescription() != null) {
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, events.getDescription());
        }

        if (events.getStart() != null) {
            Date startDate = DateUtils.parseDateToShow(events.getStart(), "EN");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateString = dateFormat.format(startDate);
            Date formattedDate = null;
            try {
                formattedDate = dateFormat.parse(formattedDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long startTime = 0;
            if (formattedDate != null) {
                startTime = formattedDate.getTime();
            }
            calIntent.putExtra(CalendarContract.Events.DTSTART, startTime);
        } else {
            calIntent.putExtra(CalendarContract.Events.DTSTART, "UNKNOWN");
        }
        if (events.getEnd() != null) {
            Date endDate = DateUtils.parseDateToShow(events.getEnd(), "EN");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateString = dateFormat.format(endDate);
            Date formattedDate = null;
            try {
                formattedDate = dateFormat.parse(formattedDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long endTime = 0;
            if (formattedDate != null) {
                endTime = formattedDate.getTime();
            }
            calIntent.putExtra(CalendarContract.Events.DTEND, endTime);
        } else {
            calIntent.putExtra(CalendarContract.Events.DURATION, String.valueOf(events.getDuration()));
        }

        calIntent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Rome");
        context.startActivity(calIntent);
    }
}
