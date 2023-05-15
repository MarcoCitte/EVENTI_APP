package com.example.eventiapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsAndPlacesPagerAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentContainerEventsPlacesCalendarBinding;
import com.example.eventiapp.databinding.FragmentSearchBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;

    public SearchFragment() {
    }

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
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        return fragmentSearchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle=null;
        String isPlace=null;
        if(getArguments()!=null) {
            String sort = getArguments().getString("sort");
            bundle = new Bundle();
            bundle.putString("sort", sort);
            isPlace=getArguments().getString("place");
        }

        tabLayout=fragmentSearchBinding.tabLayout;
        viewPager2=fragmentSearchBinding.viewPager;
        eventsAndPlacesPagerAdapter =new EventsAndPlacesPagerAdapter(this);
        eventsAndPlacesPagerAdapter.setBundle(bundle);
        viewPager2.setAdapter(eventsAndPlacesPagerAdapter);

        if(isPlace!=null){  //HA SCELTO SEE ALL VENUES
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