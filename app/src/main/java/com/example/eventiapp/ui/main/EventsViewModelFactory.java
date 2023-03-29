package com.example.eventiapp.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventiapp.repository.events.IEventsRepository;
import com.example.eventiapp.repository.events.IEventsRepositoryWithLiveData;

public class EventsViewModelFactory implements ViewModelProvider.Factory {
    private final IEventsRepositoryWithLiveData iEventsRepository;

    public EventsViewModelFactory(IEventsRepositoryWithLiveData iEventsRepository){
        this.iEventsRepository=iEventsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EventsViewModel(iEventsRepository);
    }


}
