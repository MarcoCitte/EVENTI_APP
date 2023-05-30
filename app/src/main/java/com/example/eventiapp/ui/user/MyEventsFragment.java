package com.example.eventiapp.ui.user;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FIRST_LOADING;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.EventsListAdapter;
import com.example.eventiapp.databinding.FragmentAddEventBinding;
import com.example.eventiapp.databinding.FragmentMyEventsBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.ErrorMessageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MyEventsFragment extends Fragment {

    private static final String TAG = MyEventsFragment.class.getSimpleName();
    private FragmentMyEventsBinding fragmentMyEventsBinding;

    private List<Events> eventsList;
    private EventsListAdapter eventsListAdapter;
    private ProgressBar progressBar;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private UserViewModel userViewModel;


    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static MyEventsFragment newInstance() {
        return new MyEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventsList = new ArrayList<>();
        eventsAndPlacesViewModel = new ViewModelProvider(requireActivity()).get(EventsAndPlacesViewModel.class);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyEventsBinding = FragmentMyEventsBinding.inflate(inflater, container, false);
        return fragmentMyEventsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         fragmentMyEventsBinding.createEventButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Navigation.findNavController(requireView()).navigate(R.id.action_containerMyEventsAndPlaces_to_addEventFragment);
             }
         });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
