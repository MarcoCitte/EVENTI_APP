package com.example.eventiapp.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventiapp.ui.main.MyEventsFragment;
import com.example.eventiapp.ui.main.MyPlacesFragment;


public class MySavingsPagerAdapter extends FragmentStateAdapter {


    public MySavingsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyEventsFragment();
            case 1:
                return new MyPlacesFragment();
            default:
                return new MyEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
