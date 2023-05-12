package com.example.eventiapp.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventiapp.ui.main.AllEventsFragment;
import com.example.eventiapp.ui.main.AllPlacesFragment;
import com.example.eventiapp.ui.main.CalendarFragment;

public class EventsAndPlacesPagerAdapter extends FragmentStateAdapter {

    private Bundle bundle;

    public EventsAndPlacesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AllEventsFragment.newInstance(bundle);
            case 1:
                return AllPlacesFragment.newInstance(bundle);
            case 2:
                return new CalendarFragment();
            default:
                return AllEventsFragment.newInstance(bundle);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

}
