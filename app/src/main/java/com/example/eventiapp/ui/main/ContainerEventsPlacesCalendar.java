package com.example.eventiapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventiapp.adapter.EventsAndPlacesPagerAdapter;
import com.example.eventiapp.databinding.FragmentContainerEventsPlacesCalendarBinding;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ContainerEventsPlacesCalendar extends Fragment {

    FragmentContainerEventsPlacesCalendarBinding fragmentbinding;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    EventsAndPlacesPagerAdapter eventsAndPlacesPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentbinding = FragmentContainerEventsPlacesCalendarBinding.inflate(inflater, container, false);
        return fragmentbinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle=null;
        boolean viewAllPlaces=false;
        if(getArguments()!=null && (!Objects.equals(getArguments().getString("sort"), null))) {  //PROVIENE DA HOMEFRAGMENT PER ANDARE IN ALL EVENTS COL SORT
            bundle = new Bundle();
            bundle.putString("sort", getArguments().getString("sort"));
        }else if(getArguments()!=null && (!Objects.equals(getArguments().getString("place"), null))){ //PROVIENE DA HOMEFRAGMENT PER ANDARE IN ALL PLACES
            viewAllPlaces=true;
        }

        tabLayout=fragmentbinding.tabLayout;
        viewPager2=fragmentbinding.viewPager;
        eventsAndPlacesPagerAdapter =new EventsAndPlacesPagerAdapter(this);
        eventsAndPlacesPagerAdapter.setBundle(bundle);
        viewPager2.setAdapter(eventsAndPlacesPagerAdapter);

        if(viewAllPlaces){  //HA SCELTO SEE ALL PLACES
          viewPager2.setCurrentItem(1); //IMPOSTA TAB ALL PLACES
        }

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
