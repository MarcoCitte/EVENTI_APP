package com.example.eventiapp.adapter;

import static com.example.eventiapp.util.Constants.LOADING_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.PLACES_VIEW_TYPE;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.model.Place;

import java.util.List;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onPlacesItemClick(Place place);

        void onFavoriteButtonPressed(int position);
    }

    private final List<Place> placeList;
    private final Application application;
    private final PlacesRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public PlacesRecyclerViewAdapter(List<Place> placeList, Application application,
                                     PlacesRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.placeList = placeList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (placeList.get(position) == null) {
            return LOADING_VIEW_TYPE;
        } else {
            return PLACES_VIEW_TYPE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        if (viewType == PLACES_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.places_list_item, parent, false);
            return new PlacesViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.places_loading_item, parent, false);
            return new LoadingPlacesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlacesViewHolder) {
            ((PlacesViewHolder) holder).bind(placeList.get(position));
        } else if (holder instanceof LoadingPlacesViewHolder) {
            ((LoadingPlacesViewHolder) holder).activate();
        }
    }

    @Override
    public int getItemCount() {
        if (placeList != null) {
            return placeList.size();
        }
        return 0;
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

        public void bind(Place places) {
            textViewName.setText(places.getName());
            textViewType.setText(places.getType());
            textViewAddress.setText(places.getAddress());
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onPlacesItemClick(placeList.get(getAdapterPosition()));
        }
    }

    public static class LoadingPlacesViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingPlacesViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_event);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}
