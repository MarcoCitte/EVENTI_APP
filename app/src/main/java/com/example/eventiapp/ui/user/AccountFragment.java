package com.example.eventiapp.ui.user;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAccountBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModelFactory;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.LanguageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding fragmentAccountBinding;
    private String[] languages;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private UserViewModel userViewModel;
    private List<Events> eventsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IRepositoryWithLiveData eventsRepositoryWithLiveData =
                ServiceLocator.getInstance().getRepository(
                        requireActivity().getApplication()
                );

        if (eventsRepositoryWithLiveData != null) {
            eventsAndPlacesViewModel = new ViewModelProvider(
                    requireActivity(),
                    new EventsAndPlacesViewModelFactory(eventsRepositoryWithLiveData)).get(EventsAndPlacesViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
        }

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        eventsList = new ArrayList<>();
        languages = requireContext().getResources().getStringArray(R.array.languages);
        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false);
        return fragmentAccountBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsAndPlacesViewModel.getFavoriteEventsLiveData(false).observe(getViewLifecycleOwner(), result -> {
            if(result!=null){
                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                List<Events> fetchedEvents = eventsResponse.getEventsList();
                fragmentAccountBinding.numberEventsTextView.setText(String.valueOf(fetchedEvents.size()));
            }else{
                fragmentAccountBinding.numberEventsTextView.setText("0");
            }
        });

        //FAVORITE CATEGORY
        eventsAndPlacesViewModel.getFavoriteCategory().observe(getViewLifecycleOwner(), result ->{
            if(result!=null){
                fragmentAccountBinding.favoriteCategoryTextView.setText(result);
            }else{
                fragmentAccountBinding.favoriteCategoryTextView.setText(R.string.none);
            }
        });

        //LOGOUT


        fragmentAccountBinding.logoutB.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if (result.isSuccess()) {
                    Navigation.findNavController(view).navigate(
                            R.id.action_accountFragment_to_welcomeActivity);
                    requireActivity().finish();
                } else {
                    Snackbar.make(view,
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        });



        //SPINNER LANGUAGE
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentAccountBinding.languageSpinner.setAdapter(adapter);

        String language = sharedPreferencesUtil.readStringData(Constants.SHARED_PREFERENCES_FILE_NAME, Constants.SHARED_PREFERENCES_LANGUAGE);
        if(language!=null) {
            if (language.equals("IT")) {
                fragmentAccountBinding.languageSpinner.setSelection(0);
            } else {
                fragmentAccountBinding.languageSpinner.setSelection(1);
            }
        }else{
            fragmentAccountBinding.languageSpinner.setSelection(1);
        }

        fragmentAccountBinding.languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILE_NAME, Constants.SHARED_PREFERENCES_LANGUAGE, selectedLanguage);
                //LanguageUtil.setAppLanguage(requireContext(), selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
