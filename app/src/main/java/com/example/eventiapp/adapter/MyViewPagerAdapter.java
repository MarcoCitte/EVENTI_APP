package com.example.eventiapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventiapp.ui.main.AllEventsFragment;
import com.example.eventiapp.ui.main.AllPlacesFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllEventsFragment();
            case 1:
                return new AllPlacesFragment();
            default:
                return new AllEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
