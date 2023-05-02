package com.example.eventiapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentSearchBinding;

import java.util.Locale;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding fragmentSearchBinding;

    public SearchFragment() {
    }


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

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentSearchBinding.communityCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","community");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.concertsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","concerts");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.sportsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","sports");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.movieCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","movies");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.guestCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","Guest");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.publicCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","Public");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });

        fragmentSearchBinding.specialCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category","Special");
                Navigation.findNavController(view).navigate(R.id.action_searchFragment_to_categoryFragment, bundle);
            }
        });


    }
}