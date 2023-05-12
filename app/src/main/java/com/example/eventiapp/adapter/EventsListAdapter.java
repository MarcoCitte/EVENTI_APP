package com.example.eventiapp.adapter;

import android.content.Context;
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
import com.example.eventiapp.util.DateTimeUtil;

import java.util.List;

public class EventsListAdapter extends ArrayAdapter<Events> {
    private final List<Events> eventsList;
    private final int layout;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;

    /**
     * Interface to associate a listener to other elements defined in the layout
     * chosen for the ListView item (e.g., a Button).
     */
    public interface OnFavoriteButtonClickListener {
        void onFavoriteButtonClick(Events events);
    }

    public EventsListAdapter(@NonNull Context context, int layout, @NonNull List<Events> eventsList,
                           OnFavoriteButtonClickListener onDeleteButtonClickListener) {
        super(context, layout, eventsList);
        this.layout = layout;
        this.eventsList = eventsList;
        this.onFavoriteButtonClickListener = onDeleteButtonClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }

        TextView textViewTitle = convertView.findViewById(R.id.titleTextView);
        TextView textViewDate = convertView.findViewById(R.id.dateTextView);
        TextView textViewCategory=convertView.findViewById(R.id.categoryTextView);
        ImageView imageViewFavoriteEvent = convertView.findViewById(R.id.imageViewFavorite);

        imageViewFavoriteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavoriteButtonClickListener.onFavoriteButtonClick(eventsList.get(position));
            }
        });

        textViewTitle.setText(eventsList.get(position).getTitle());
        String fromTO="FROM: " + DateTimeUtil.getDate(eventsList.get(position).getStart()) + " \nTO: " + DateTimeUtil.getDate(eventsList.get(position).getStart());
        textViewDate.setText(fromTO);
        textViewCategory.setText(eventsList.get(position).getCategory());

        return convertView;
    }
}
