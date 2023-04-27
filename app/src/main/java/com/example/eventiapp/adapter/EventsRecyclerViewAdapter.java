package com.example.eventiapp.adapter;

import static com.example.eventiapp.util.Constants.EVENTS_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.LOADING_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.PLACES_VIEW_TYPE;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.util.DateTimeUtil;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int typeOfView;

    public interface OnItemClickListener {
        void onEventsItemClick(Events events);
        void onPlacesItemClick(Events events);
        void onFavoriteButtonPressed(int position);
    }

    private final List<Events> eventsList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public EventsRecyclerViewAdapter(List<Events> eventsList, Application application,int typeOfView,
                                     OnItemClickListener onItemClickListener) {
        this.eventsList = eventsList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
        this.typeOfView=typeOfView;
    }

    @Override
    public int getItemViewType(int position) {
        if(typeOfView==0) {
            if (eventsList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return EVENTS_VIEW_TYPE;
            }
        }else if(typeOfView==2){ //PLACES
            if (eventsList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return PLACES_VIEW_TYPE;
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
        } else if (viewType == PLACES_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.places_list_item, parent, false);
            return new PlacesViewHolder(view);
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
        } else if (holder instanceof PlacesViewHolder) {
            ((PlacesViewHolder) holder).bind(eventsList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (eventsList != null) {
            return eventsList.size();
        }
        return 0;
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewCategory;
        private ImageView imageViewFavoriteEvent;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_title);
            textViewDate = itemView.findViewById(R.id.textview_date);
            textViewCategory = itemView.findViewById(R.id.textview_category);
            imageViewFavoriteEvent = itemView.findViewById(R.id.imageview_favorite_event);
            itemView.setOnClickListener(this);
            //imageViewFavoriteEvent.setOnClickListener(this);
        }

        public void bind(Events events) {
            textViewTitle.setText(events.getTitle());
            //EVENTI UCI ED EVENTI PIRELLI HANGAR NON HANNO FINE DATA
            if(events.getEnd()!=null) {
                String fromTO = DateTimeUtil.getDate(events.getStart()) + " - " + DateTimeUtil.getDate(events.getStart());
                textViewDate.setText(fromTO);
            }else{
                textViewDate.setText(events.getStart());
            }
            textViewCategory.setText(events.getCategory());
            //setImageViewFavoriteEvent(eventsList.get(getAdapterPosition()).isFavorite());
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageview_favorite_event) {
                //setImageViewFavoriteEvent(!eventsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
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

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewName;
        private TextView textViewType;
        private TextView textViewAddress;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textview_name);
            textViewType = itemView.findViewById(R.id.textview_type);
            textViewAddress = itemView.findViewById(R.id.textview_address);
            itemView.setOnClickListener(this);
        }

        public void bind(Events events) {
            if(!events.getPlaces().isEmpty() && !events.getCategory().equals("severe-weather")) {
                textViewName.setText(events.getPlaces().get(0).getName());
                textViewType.setText(events.getPlaces().get(0).getType());
                textViewAddress.setText(events.getPlaces().get(0).getAddress());
            }else{
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onPlacesItemClick(eventsList.get(getAdapterPosition()));
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
