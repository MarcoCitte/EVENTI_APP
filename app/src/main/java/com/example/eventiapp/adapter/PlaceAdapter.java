package com.example.eventiapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.eventiapp.model.Place;

import java.util.List;

// Creazione della classe personalizzata dell'ArrayAdapter
public class PlaceAdapter extends ArrayAdapter<Place> {

    private LayoutInflater inflater;

    public PlaceAdapter(Context context, List<Place> places) {
        super(context, 0, places);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Place place = getItem(position);

        if (place != null) {
            String placeNameAndAddress = place.getName() + " - " + place.getAddress();
            textView.setText(placeNameAndAddress);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Place place = getItem(position);

        if (place != null) {
            String placeNameAndAddress = place.getName() + " - " + place.getAddress();
            textView.setText(placeNameAndAddress);
        }

        return convertView;
    }
}
