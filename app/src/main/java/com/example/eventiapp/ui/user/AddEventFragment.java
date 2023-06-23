package com.example.eventiapp.ui.user;

import static android.app.Activity.RESULT_OK;
import static com.example.eventiapp.util.Constants.REQUEST_CODE_PICK_IMAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAddEventBinding;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.model.EventSource;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModelFactory;
import com.example.eventiapp.ui.main.MyDialogEventsFragment;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.LanguageUtil;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddEventFragment extends Fragment {

    private FragmentAddEventBinding fragmentAddEventBinding;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;
    private UserViewModel userViewModel;

    private static final String TAG = AddEventFragment.class.getSimpleName();
    private MaterialDatePicker<Long> materialDatePicker;
    private MaterialTimePicker materialTimePicker;
    private String[] categories;

    private Uri imageUri;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String namePlace;
    private String idPlace;
    private String address;
    private List<Double> coordinates;
    private boolean isPrivate;
    private boolean googlePlace;


    public AddEventFragment() {
    }

    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

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

        categories = requireContext().getResources().getStringArray(R.array.categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddEventBinding = FragmentAddEventBinding.inflate(inflater, container, false);
        return fragmentAddEventBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialDatePicker.Builder<Long> builderStartDate = MaterialDatePicker.Builder.datePicker();
        builderStartDate.setTitleText("Select a start date");

        MaterialDatePicker.Builder<Long> builderEndDate = MaterialDatePicker.Builder.datePicker();
        builderEndDate.setTitleText("Select an end date");

        MaterialTimePicker.Builder builderStartTime = new MaterialTimePicker.Builder();
        builderStartTime.setTitleText("Select a start time");
        builderStartTime.setTimeFormat(TimeFormat.CLOCK_24H);

        MaterialTimePicker.Builder builderEndTime = new MaterialTimePicker.Builder();
        builderEndTime.setTitleText("Select an end time");
        builderEndTime.setTimeFormat(TimeFormat.CLOCK_24H);


        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


        //IMAGEVIEW

        fragmentAddEventBinding.eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });


        //SPINNER CATEGORIES
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentAddEventBinding.categoriesSpinner.setAdapter(adapter);

        fragmentAddEventBinding.editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker = builderStartDate.build();
                materialDatePicker.show(getParentFragmentManager(), TAG);
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Date start = new Date(selection);
                        start = DateUtils.parseDate(String.valueOf(start), "EN");
                        if (start != null) {
                            fragmentAddEventBinding.editTextStartDate.setText(" " + formatter.format(start));
                        }
                    }
                });
            }
        });

        fragmentAddEventBinding.editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker = builderEndDate.build();
                materialDatePicker.show(getParentFragmentManager(), TAG);
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Date end = new Date(selection);
                        end = DateUtils.parseDate(String.valueOf(end), "EN");
                        if (end != null) {
                            fragmentAddEventBinding.editTextEndDate.setText(" " + formatter.format(end));
                        }
                    }
                });
            }
        });

        fragmentAddEventBinding.editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialTimePicker = builderStartTime.build();
                materialTimePicker.show(getParentFragmentManager(), TAG);
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        startTime = hour + ":" + minute;
                        fragmentAddEventBinding.editTextStartTime.setText(startTime);
                    }
                });
            }
        });


        fragmentAddEventBinding.editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialTimePicker = builderEndTime.build();
                materialTimePicker.show(getParentFragmentManager(), TAG);
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        endTime = hour + ":" + minute;
                        fragmentAddEventBinding.editTextEndTime.setText(endTime);
                    }
                });
            }
        });


        //PLACE

        fragmentAddEventBinding.placeSourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.personalPlacesRadioButton) {
                    //MOSTRA I POSTI CREATI DALL'UTENTE
                    fragmentAddEventBinding.layoutPersonalPlaces.setVisibility(View.VISIBLE);
                    fragmentAddEventBinding.layoutGooglePlaces.setVisibility(View.GONE);
                    googlePlace = false;
                } else {
                    //MOSTRA QUELLI DI GOOGLE PLACES
                    fragmentAddEventBinding.layoutPersonalPlaces.setVisibility(View.GONE);
                    fragmentAddEventBinding.layoutGooglePlaces.setVisibility(View.VISIBLE);
                    googlePlace = true;

                    AutocompleteSupportFragment autocompleteFragment = AutocompleteSupportFragment.newInstance();
                    autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
                    autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME));

                    // Aggiungi il fragment all'activity
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.autocompleteContainer, autocompleteFragment)
                            .commit();

                    PlaceDetailsSource.fetchAutoCompletePlaces(autocompleteFragment, new PlaceDetailsSource.PlaceAutoCompleteListener() {
                        @Override
                        public void onPlaceAutoCompleteListener(com.google.android.libraries.places.api.model.Place place) {
                            address = place.getAddress();
                            namePlace = place.getName();
                            idPlace = place.getId();
                            coordinates=new ArrayList<>();
                            coordinates.add(place.getLatLng().latitude);
                            coordinates.add(place.getLatLng().longitude);
                        }

                        @Override
                        public void onError(String message) {

                        }
                    });
                }
            }
        });


        //ALL DAY CHECKBOX
        fragmentAddEventBinding.allDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fragmentAddEventBinding.editTextStartTime.setVisibility(View.GONE);
                    fragmentAddEventBinding.editTextEndDate.setVisibility(View.GONE);
                    fragmentAddEventBinding.editTextEndTime.setVisibility(View.GONE);
                    fragmentAddEventBinding.endTextView.setVisibility(View.GONE);
                } else {
                    fragmentAddEventBinding.editTextStartTime.setVisibility(View.VISIBLE);
                    fragmentAddEventBinding.editTextEndDate.setVisibility(View.VISIBLE);
                    fragmentAddEventBinding.editTextEndTime.setVisibility(View.VISIBLE);
                    fragmentAddEventBinding.endTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        //ADD BUTTON
        fragmentAddEventBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = String.valueOf(fragmentAddEventBinding.editTextTitle.getText());
                description = String.valueOf(fragmentAddEventBinding.editTextDescription.getText());
                startDate = String.valueOf(fragmentAddEventBinding.editTextStartDate.getText());
                endDate = String.valueOf(fragmentAddEventBinding.editTextEndDate.getText());
                startTime = String.valueOf(fragmentAddEventBinding.editTextStartTime.getText());
                endTime = String.valueOf(fragmentAddEventBinding.editTextEndTime.getText());

                boolean isOk = true;
                if (title == null || title.isEmpty()) {
                    fragmentAddEventBinding.editTextTitle.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (description == null || description.isEmpty()) {
                    fragmentAddEventBinding.editTextDescription.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (fragmentAddEventBinding.allDayCheckBox.isChecked()) {
                    if (startDate == null || startDate.isEmpty()) {
                        fragmentAddEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                } else {
                    if (startDate == null || startDate.isEmpty()) {
                        fragmentAddEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (endDate == null || endDate.isEmpty()) {
                        fragmentAddEventBinding.editTextEndTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (startTime == null || startTime.isEmpty()) {
                        fragmentAddEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (endTime == null || endTime.isEmpty()) {
                        fragmentAddEventBinding.editTextEndTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                }

                if (address == null || address.isEmpty()) {
                    if (googlePlace) {
                        fragmentAddEventBinding.placeTextView.setError(getString(R.string.field_mandatory));
                    } else {
                        fragmentAddEventBinding.placeTextView1.setError(getString(R.string.field_mandatory));
                    }
                    isOk = true;
                }

                if (fragmentAddEventBinding.checkBoxPrivate.isChecked()) {
                    isPrivate = true;
                } else {
                    isPrivate = false;
                }

                if (isOk) {
                    //AGGIUNGI EVENTO
                    Events event = new Events();
                    event.setCreatorEmail(userViewModel.getLoggedUser().getEmail());
                    event.setTitle(title);
                    event.setDescription(description);
                    if (fragmentAddEventBinding.allDayCheckBox.isChecked()) {
                        event.setStart(startDate.trim());
                    } else {
                        event.setStart(startDate.trim() + "userH" + startTime);
                        event.setEnd(endDate.trim() + "userH" + endTime);
                    }
                    event.setTimezone("Europe/Rome");
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            event.setEventSource(new EventSource(null, String.valueOf(bitmap)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    event.setCoordinates(coordinates);

                    //SALVATAGGIO CATEGORIA IN INGLESE
                    Resources res = getResources();
                    Configuration conf = res.getConfiguration();
                    conf.locale = Locale.ENGLISH;
                    res.updateConfiguration(conf, null);
                    String[] categoriesEnglish = res.getStringArray(R.array.categories);
                    int selectedCategoryIndex = fragmentAddEventBinding.categoriesSpinner.getSelectedItemPosition();
                    String category = categoriesEnglish[selectedCategoryIndex];
                    event.setCategory(category.toLowerCase(Locale.ROOT));

                    List<Place> placeList = new ArrayList<>();
                    Place place = new Place("idPlace", "namePlace", "venue", "address");
                    placeList.add(place);
                    event.setPlaces(placeList);
                    event.setPrivate(isPrivate);
                    eventsAndPlacesViewModel.addEvent(event);
                    Navigation.findNavController(requireView()).navigate(R.id.action_addEventFragment_to_containerMyEventsAndPlaces);
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            getString(R.string.event_added), Snackbar.LENGTH_SHORT).show();

                }
            }
        });

        fragmentAddEventBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_addEventFragment_to_containerMyEventsAndPlaces);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri;
                    fragmentAddEventBinding.eventImage.setImageURI(imageUri);
                }
            }
        }
    }
}