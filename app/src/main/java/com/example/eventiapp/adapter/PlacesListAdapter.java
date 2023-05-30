package com.example.eventiapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.util.DateTimeUtil;

import java.util.List;

public class PlacesListAdapter extends ArrayAdapter<Place> {

    private final List<Place> placesList;
    private final int layout;
    private final OnFavoriteButtonClickListener2 onFavoriteButtonClickListener2;

    public interface OnFavoriteButtonClickListener2 {
        void onFavoriteButtonClick2(Place place);
    }

    public PlacesListAdapter(@NonNull Context context, int layout, @NonNull List<Place> placesList,
                             OnFavoriteButtonClickListener2 onDeleteButtonClickListener2) {
        super(context, layout, placesList);
        this.layout = layout;
        this.placesList = placesList;
        this.onFavoriteButtonClickListener2 = onDeleteButtonClickListener2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textview_name);
        TextView textViewAddress = convertView.findViewById(R.id.textview_address);
        ImageView imageViewFavoriteEvent = convertView.findViewById(R.id.imageViewFavorite);

        imageViewFavoriteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "onClick: " + placesList.get(position).hashCode());
                onFavoriteButtonClickListener2.onFavoriteButtonClick2(placesList.get(position));
            }
        });

        textViewName.setText(placesList.get(position).getName());
        textViewAddress.setText(DateTimeUtil.getDate(placesList.get(position).getAddress()));

        return convertView;
    }
}
