package com.example.eventiapp.adapter;

import static com.example.eventiapp.util.Constants.LOADING_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.MAX_ITEMS;
import static com.example.eventiapp.util.Constants.PLACES2_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.PLACES3_VIEW_TYPE;
import static com.example.eventiapp.util.Constants.PLACES_VIEW_TYPE;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
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
import com.example.eventiapp.model.Place;
import com.example.eventiapp.source.google.PlaceDetailsSource;

import java.util.List;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int typeOfView;

    public interface OnItemClickListener {
        void onPlacesItemClick(Place place);

        void onShareButtonPressed(Place place);

        void onFavoriteButtonPressed(int position);

        void onModePlaceButtonPressed(Place place);

        void onDeletePlaceButtonPressed(Place place);
    }

    private final List<Place> placeList;
    private final Application application;
    private final PlacesRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public PlacesRecyclerViewAdapter(List<Place> placeList, Application application, int typeOfView,
                                     PlacesRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.placeList = placeList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
        this.typeOfView = typeOfView;
    }


    @Override
    public int getItemViewType(int position) {
        if (typeOfView == 2) {
            if (placeList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return PLACES_VIEW_TYPE;
            }
        } else if (typeOfView == 4) { //PLACES 2
            if (placeList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return PLACES2_VIEW_TYPE;
            }
        } else if (typeOfView == 6) {  //PLACE 3
            if (placeList.get(position) == null) {
                return LOADING_VIEW_TYPE;
            } else {
                return PLACES3_VIEW_TYPE;
            }
        }
        return PLACES_VIEW_TYPE;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == PLACES_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.places_list_item, parent, false);
            return new PlacesViewHolder(view);
        } else if (viewType == PLACES2_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.place2_list_item, parent, false);
            return new PlacesRecyclerViewAdapter.Places2ViewHolder(view);
        } else if (viewType == PLACES3_VIEW_TYPE) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.places3_list_item, parent, false);
            return new PlacesRecyclerViewAdapter.Places3ViewHolder(view);
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
        } else if (holder instanceof Places2ViewHolder) {
            ((Places2ViewHolder) holder).bind(placeList.get(position));
        } else if (holder instanceof Places3ViewHolder) {
            ((Places3ViewHolder) holder).bind(placeList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (placeList != null && (typeOfView == 2 || typeOfView == 6)) { //PLACE 1 e PLACE 2
            return placeList.size();
        } else if (placeList != null && typeOfView == 4) { //PLACE 2
            return Math.min(placeList.size(), MAX_ITEMS);
        }
        return 0;
    }

    public class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewName;
        private final TextView textViewAddress;
        private final TextView textViewDistance;
        private final ImageView imageViewPlace;
        private final ImageView imageViewFavorite;


        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.nameTextView);
            textViewDistance = itemView.findViewById(R.id.distanceTextView);
            textViewAddress = itemView.findViewById(R.id.addressTextView);
            imageViewPlace = itemView.findViewById(R.id.imageViewPlace);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            ImageView imageViewShare = itemView.findViewById(R.id.imageViewShare);

            itemView.setOnClickListener(this);
            imageViewFavorite.setOnClickListener(this);
            imageViewShare.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Place places) {
            textViewName.setText(places.getName());
            textViewAddress.setText(places.getAddress());
            textViewDistance.setText("5.2km");
            if (places.getImages() != null && !places.getImages().isEmpty()) {
                PlaceDetailsSource.fetchPlacePhotos(places.getImages(), true, new PlaceDetailsSource.PlacePhotosListener() {
                    @Override
                    public void onPlacePhotosListener(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageViewPlace.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("ERROR", message);
                        imageViewPlace.setVisibility(View.GONE);
                    }
                });
            } else {
                imageViewPlace.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageViewFavorite) {
                //setImageViewFavoritePlace(!placeList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else if (v.getId() == R.id.imageViewShare) {
                onItemClickListener.onShareButtonPressed(placeList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onPlacesItemClick(placeList.get(getAdapterPosition()));
            }
        }

        private void setImageViewFavoritePlace(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }

    public class Places2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewName;
        private final TextView textViewAddress;
        private final TextView textViewDistance;
        private final ImageView imageViewPlace;
        private final ImageView imageViewFavorite;

        public Places2ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.nameTextView);
            textViewDistance = itemView.findViewById(R.id.distanceTextView);
            textViewAddress = itemView.findViewById(R.id.addressTextView);
            imageViewPlace = itemView.findViewById(R.id.imageViewPlace);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            ImageView imageViewShare = itemView.findViewById(R.id.imageViewShare);

            itemView.setOnClickListener(this);
            imageViewFavorite.setOnClickListener(this);
            imageViewShare.setOnClickListener(this);

        }

        @SuppressLint("SetTextI18n")
        public void bind(Place places) {
            textViewName.setText(places.getName());
            textViewAddress.setText(places.getAddress());
            textViewDistance.setText("5.2km");
            if (places.getImages() != null && !places.getImages().isEmpty()) {
                PlaceDetailsSource.fetchPlacePhotos(places.getImages(), true, new PlaceDetailsSource.PlacePhotosListener() {
                    @Override
                    public void onPlacePhotosListener(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageViewPlace.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("ERROR", message);
                        imageViewPlace.setVisibility(View.GONE);
                    }
                });
            } else {
                imageViewPlace.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageViewFavorite) {
                //setImageViewFavoritePlace(!placeList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else if (v.getId() == R.id.imageViewShare) {
                onItemClickListener.onShareButtonPressed(placeList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onPlacesItemClick(placeList.get(getAdapterPosition()));
            }
        }

        private void setImageViewFavoritePlace(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }

    public class Places3ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView textViewName;
        private final TextView textViewAddress;
        private final TextView textViewDistance;
        private final ImageView imageViewPlace;
        private final ImageView imageViewFavorite;
        private final ImageView imageViewMode;
        private final ImageView imageViewDelete;

        public Places3ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.nameTextView);
            textViewDistance = itemView.findViewById(R.id.distanceTextView);
            textViewAddress = itemView.findViewById(R.id.addressTextView);
            imageViewPlace = itemView.findViewById(R.id.imageViewPlace);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            imageViewMode = itemView.findViewById(R.id.imageViewMode);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);
            ImageView imageViewShare = itemView.findViewById(R.id.imageViewShare);

            itemView.setOnClickListener(this);
            imageViewFavorite.setOnClickListener(this);
            imageViewShare.setOnClickListener(this);
            imageViewMode.setOnClickListener(this);
            imageViewDelete.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Place places) {
            textViewName.setText(places.getName());
            textViewAddress.setText(places.getAddress());
            textViewDistance.setText("5.2km");
            if (places.getImages() != null && !places.getImages().isEmpty()) {
                PlaceDetailsSource.fetchPlacePhotos(places.getImages(), true, new PlaceDetailsSource.PlacePhotosListener() {
                    @Override
                    public void onPlacePhotosListener(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageViewPlace.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("ERROR", message);
                        imageViewPlace.setVisibility(View.GONE);
                    }
                });
            } else {
                imageViewPlace.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageViewFavorite) {
                //setImageViewFavoritePlace(!placeList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else if (v.getId() == R.id.imageViewShare) {
                onItemClickListener.onShareButtonPressed(placeList.get(getAdapterPosition()));
            } else if (v.getId() == R.id.imageViewMode) {
                onItemClickListener.onModePlaceButtonPressed(placeList.get(getAdapterPosition()));
            } else if (v.getId() == R.id.imageViewDelete) {
                onItemClickListener.onDeletePlaceButtonPressed(placeList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onPlacesItemClick(placeList.get(getAdapterPosition()));
            }
        }

        private void setImageViewFavoritePlace(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_24));
            } else {
                imageViewFavorite.setImageDrawable(
                        AppCompatResources.getDrawable(application,
                                R.drawable.ic_baseline_favorite_border_24));
            }
        }
    }


    public static class LoadingPlacesViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;

        LoadingPlacesViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressbar_loading_place);
        }

        public void activate() {
            progressBar.setIndeterminate(true);
        }
    }
}
