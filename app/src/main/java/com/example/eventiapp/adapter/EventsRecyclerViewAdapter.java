package com.example.eventiapp.adapter;

import static com.example.eventiapp.util.Constants.EVENTS_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.LOADING_VIEW_TYPE;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.util.DateTimeUtil;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onEventsItemClick(Events events);

        void onExportButtonPressed(Events events);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Events> eventsList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public EventsRecyclerViewAdapter(List<Events> eventsList, Application application,
                                     OnItemClickListener onItemClickListener) {
        this.eventsList = eventsList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (eventsList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return EVENTS_VIEW_TYPE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == EVENTS_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.events_list_item, parent, false);
            return new EventsViewHolder(view);
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
        private MaterialButton exportButton;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_title);
            textViewDate = itemView.findViewById(R.id.textview_date);
            textViewCategory = itemView.findViewById(R.id.textview_category);
            imageViewFavoriteEvent = itemView.findViewById(R.id.imageview_favorite_event);
            exportButton = itemView.findViewById(R.id.buttonExport);
            itemView.setOnClickListener(this);
            exportButton.setOnClickListener(this);
            //imageViewFavoriteEvent.setOnClickListener(this);
        }

        public void bind(Events events) {
            textViewTitle.setText(events.getTitle());
            //EVENTI UCI ED EVENTI PIRELLI HANGAR NON HANNO FINE DATA
            if (events.getEnd() != null) {
                String fromTO = DateTimeUtil.getDate(events.getStart()) + " - " + DateTimeUtil.getDate(events.getStart());
                textViewDate.setText(fromTO);
            } else {
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
            } else if (v.getId() == R.id.buttonExport) {
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
