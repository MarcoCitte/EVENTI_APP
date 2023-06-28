package com.example.eventiapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventiapp.ui.user.MyEventsFragment;
import com.example.eventiapp.ui.user.MyPlacesFragment;

public class MyEventsAndPlacesPagerAdapter extends FragmentStateAdapter {
    public MyEventsAndPlacesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new MyPlacesFragment();
        }
        return new MyEventsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
