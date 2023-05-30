package com.example.eventiapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsAndPlacesPagerAdapter;
import com.example.eventiapp.adapter.MySavingsPagerAdapter;
import com.example.eventiapp.databinding.FragmentContainerEventsPlacesCalendarBinding;
import com.example.eventiapp.databinding.FragmentMySavingsBinding;
import com.google.android.material.tabs.TabLayout;

public class MySavingsFragment extends Fragment {

    FragmentMySavingsBinding fragmentbinding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MySavingsPagerAdapter mySavingsPagerAdapter;


    public MySavingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentbinding = FragmentMySavingsBinding.inflate(inflater, container, false);
        return fragmentbinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tabLayout=fragmentbinding.tabLayout;
        viewPager2=fragmentbinding.viewPager;
        mySavingsPagerAdapter =new MySavingsPagerAdapter(this);
        viewPager2.setAdapter(mySavingsPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

    }

}