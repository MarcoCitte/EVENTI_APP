package com.example.eventiapp.adapter;

import static com.example.eventiapp.util.Constants.EVENTS2_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.EVENTS_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.LOADING_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.MAX_ITEMS;

import android.app.Application;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.util.DateTimeUtil;
import com.example.eventiapp.util.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int typeOfView;

    public interface OnItemClickListener {
        void onEventsItemClick(Events events);

        void onExportButtonPressed(Events events);

        void onShareButtonPressed(Events events);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Events> eventsList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public EventsRecyclerViewAdapter(List<Events> eventsList, Application application, int typeOfView,
                                     OnItemClickListener onItemClickListener) {
        this.eventsList = eventsList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
        this.typeOfView = typeOfView;
    }


    @Override
    public int getItemViewType(int position) {
        if (typeOfView == 0) {
            if (eventsList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return EVENTS_VIEW_TYPE;
            }
        } else if (typeOfView == 3) { //EVENTS 2
            if (eventsList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return EVENTS2_VIEW_TYPE;
            }
        }
        return EVENTS_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == EVENTS_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.events_list_item, parent, false);
            return new EventsViewHolder(view);
        } else if (viewType == EVENTS2_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.events2_list_item, parent, false);
            return new Events2ViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.events_loading_item, parent, false);
            return new LoadingEventsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventsViewHolder) {
            ((EventsViewHolder) holder).bind(eventsList.get(position));
        } else if (holder instanceof LoadingEventsViewHolder) {
            ((LoadingEventsViewHolder) holder).activate();
        } else if (holder instanceof Events2ViewHolder) {
            ((Events2ViewHolder) holder).bind(eventsList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (eventsList != null && typeOfView == 0) { //EVENTS
            return eventsList.size();
        } else if (eventsList != null && typeOfView == 3) { //EVENTS2
            if (eventsList.size() >= MAX_ITEMS) {
                return MAX_ITEMS;
            } else {
                return eventsList.size();
            }
        }
        return 0;
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewCategory;
        private TextView textViewPlace;
        private ImageView imageViewEvent;
        private ImageView imageViewFavoriteEvent;
        private ImageView imageViewShare;
        private ImageView imageViewExport;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTextView);
            textViewDate = itemView.findViewById(R.id.dateTextView);
            textViewCategory = itemView.findViewById(R.id.categoryTextView);
            textViewPlace = itemView.findViewById(R.id.placeTextView);
            imageViewEvent = itemView.findViewById(R.id.imageViewEvent);
            imageViewFavoriteEvent = itemView.findViewById(R.id.imageViewFavorite);
            imageViewShare = itemView.findViewById(R.id.imageViewShare);
            imageViewExport = itemView.findViewById(R.id.imageViewExport);
            itemView.setOnClickListener(this);
            imageViewShare.setOnClickListener(this);
            imageViewFavoriteEvent.setOnClickListener(this);
            imageViewExport.setOnClickListener(this);
        }

        public void bind(Events events) {
            textViewTitle.setText(events.getTitle());
            //EVENTI UCI ED EVENTI PIRELLI HANGAR NON HANNO FINE DATA
            if (events.getEnd() != null && !Objects.equals(events.getStart(), events.getEnd())) {
                String dateStart = events.getStart();
                String dateEnd = events.getEnd();
                Date date1 = DateUtils.parseDateToShow(dateStart, "EN");
                Date date2 = DateUtils.parseDateToShow(dateEnd, "EN");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                String formattedDate = outputFormat.format(date1) + " - " + outputFormat.format(date2);
                textViewDate.setText(formattedDate);
                textViewDate.setTextSize(13);
            } else {
                String date = events.getStart();
                Date date1 = DateUtils.parseDateToShow(date, "EN");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                String formattedDate = outputFormat.format(date1);
                textViewDate.setText(formattedDate);
            }
            textViewCategory.setText(events.getCategory());
            if (events.getPlaces() != null && !events.getPlaces().isEmpty()) {
                textViewPlace.setText(events.getPlaces().get(0).getName());
            } else {
                textViewPlace.setVisibility(View.GONE);
            }
            if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
                Glide.with(itemView).load(events.getEventSource().getUrlPhoto()).into(imageViewEvent);
            } else {
                imageViewEvent.setVisibility(View.GONE);
            }
            //setImageViewFavoriteEvent(eventsList.get(getAdapterPosition()).isFavorite());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageViewFavorite) {
                //setImageViewFavoriteEvent(!eventsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else if (v.getId() == R.id.imageViewShare) {
                onItemClickListener.onShareButtonPressed(eventsList.get(getAdapterPosition()));
            } else if (v.getId() == R.id.imageViewExport) {
                onItemClickListener.onExportButtonPressed(eventsList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onEventsItemClick(eventsList.get(getAdapterPosition()));
            }
        }

        private void setImageViewFavoriteEvent(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavoriteEvent.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavoriteEvent.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }


    public class Events2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewTitle;
        private TextView textViewCategory;
        private TextView textViewPlace;
        private TextView textViewDate;
        private ImageView imageViewEvent;
        private ImageView imageViewFavoriteEvent;
        private ImageView imageViewShareEvent;

        public Events2ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleTextView);
            textViewDate = itemView.findViewById(R.id.dateTextView);
            textViewCategory = itemView.findViewById(R.id.categoryTextView);
            textViewPlace = itemView.findViewById(R.id.placeTextView);
            imageViewEvent = itemView.findViewById(R.id.imageViewEvent);
            imageViewFavoriteEvent = itemView.findViewById(R.id.imageViewFavorite);
            imageViewShareEvent = itemView.findViewById(R.id.imageViewShare);
            itemView.setOnClickListener(this);
            imageViewFavoriteEvent.setOnClickListener(this);
            imageViewShareEvent.setOnClickListener(this);
        }

        public void bind(Events events) {
            textViewTitle.setText(events.getTitle());
            textViewCategory.setText(events.getCategory());
            String date = events.getStart();
            Date date1 = DateUtils.parseDateToShow(date, "EN");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            String formattedDate = outputFormat.format(date1);
            textViewDate.setText(formattedDate);

            if (events.getPlaces() != null && !events.getPlaces().isEmpty()) {
                textViewPlace.setText(events.getPlaces().get(0).getName());
            } else {
                textViewPlace.setVisibility(View.GONE);
            }
            if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
                Glide.with(itemView).load(events.getEventSource().getUrlPhoto()).into(imageViewEvent);
            } else {
                imageViewEvent.setVisibility(View.GONE);
            }
            //setImageViewFavoriteEvent(eventsList.get(getAdapterPosition()).isFavorite());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageViewFavorite) {
                //setImageViewFavoriteEvent(!eventsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else if (v.getId() == R.id.imageViewShare) {
                onItemClickListener.onShareButtonPressed(eventsList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onEventsItemClick(eventsList.get(getAdapterPosition()));
            }
        }

        private void setImageViewFavoriteEvent(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavoriteEvent.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavoriteEvent.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }


    public static class LoadingEventsViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingEventsViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_event);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }


}
