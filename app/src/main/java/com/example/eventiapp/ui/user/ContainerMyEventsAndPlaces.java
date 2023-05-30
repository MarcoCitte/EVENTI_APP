package com.example.eventiapp.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.eventiapp.adapter.MyEventsAndPlacesPagerAdapter;
import com.example.eventiapp.databinding.FragmentContainerMyEventsAndPlacesBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ContainerMyEventsAndPlaces extends Fragment {

    FragmentContainerMyEventsAndPlacesBinding fragmentbinding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyEventsAndPlacesPagerAdapter myEventsAndPlacesPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentbinding = FragmentContainerMyEventsAndPlacesBinding.inflate(inflater, container, false);
        return fragmentbinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = fragmentbinding.tabLayout;
        viewPager2 = fragmentbinding.viewPager;
        myEventsAndPlacesPagerAdapter = new MyEventsAndPlacesPagerAdapter(this);
        viewPager2.setAdapter(myEventsAndPlacesPagerAdapter);


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
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}
