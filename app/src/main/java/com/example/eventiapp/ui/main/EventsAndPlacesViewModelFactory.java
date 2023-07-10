package com.example.eventiapp.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventiapp.repository.eventsAndPlaces.IRepositoryWithLiveData;

public class EventsAndPlacesViewModelFactory implements ViewModelProvider.Factory {
    private final IRepositoryWithLiveData iRepository;

    public EventsAndPlacesViewModelFactory(IRepositoryWithLiveData iRepository){
        this.iRepository=iRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EventsAndPlacesViewModel(iRepository);
    }

}
