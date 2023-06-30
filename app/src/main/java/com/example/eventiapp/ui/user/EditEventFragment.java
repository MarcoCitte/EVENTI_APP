package com.example.eventiapp.ui.user;

import static android.app.Activity.RESULT_OK;
import static com.example.eventiapp.util.Constants.REQUEST_CODE_PICK_IMAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.example.eventiapp.R;
import com.example.eventiapp.adapter.PlaceAdapter;
import com.example.eventiapp.databinding.FragmentEditEventBinding;
import com.example.eventiapp.model.EventSource;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.Place;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.repository.user.IUserRepository;
import com.example.eventiapp.source.google.PlaceDetailsSource;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModel;
import com.example.eventiapp.ui.main.EventsAndPlacesViewModelFactory;
import com.example.eventiapp.ui.welcome.UserViewModel;
import com.example.eventiapp.ui.welcome.UserViewModelFactory;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EditEventFragment extends Fragment {

    private FragmentEditEventBinding fragmentEditEventBinding;
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
    private String placePhoneNumber;
    private List<PhotoMetadata> photoMetadata;
    private boolean isPrivate;
    private boolean googlePlace;

    private Events oldEvent;


    public EditEventFragment() {
    }

    public static EditEventFragment newInstance() {
        return new EditEventFragment();
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
        fragmentEditEventBinding = FragmentEditEventBinding.inflate(inflater, container, false);
        return fragmentEditEventBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BUNDLE
        assert getArguments() != null;
        Events events = getArguments().getParcelable("event");
        oldEvent = events;

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

        if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
            Glide.with(requireView()).load(events.getEventSource().getUrlPhoto()).into(fragmentEditEventBinding.eventImage);
        }

        fragmentEditEventBinding.eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
            }
        });

        //TITLE
        if (events.getTitle() != null) {
            fragmentEditEventBinding.editTextTitle.setText(events.getTitle());
        }

        //DESCRIPTION
        if (events.getDescription() != null) {
            fragmentEditEventBinding.editTextDescription.setText(events.getDescription());
        }

        //SPINNER CATEGORIES
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentEditEventBinding.categoriesSpinner.setAdapter(adapter);

        if (events.getCategory() != null) {
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            conf.locale = Locale.ENGLISH;
            res.updateConfiguration(conf, null);
            String[] categoriesEnglish = res.getStringArray(R.array.categories);
            for (int i = 0; i < categoriesEnglish.length; i++) {
                if (categoriesEnglish[i].equalsIgnoreCase(events.getCategory())) {
                    fragmentEditEventBinding.categoriesSpinner.setSelection(i);
                    break;
                }
            }
        }

        //DATE AND TIME
        String delimiter = "userH";
        if (events.getStart().contains(delimiter)) {
            fragmentEditEventBinding.allDayCheckBox.setChecked(false);
            int indexStart = events.getStart().indexOf(delimiter);
            fragmentEditEventBinding.editTextStartDate.setText(events.getStart().substring(0, indexStart));
            fragmentEditEventBinding.editTextStartTime.setText(events.getStart().substring(indexStart + 5));
            int indexEnd = events.getEnd().indexOf(delimiter);
            fragmentEditEventBinding.editTextEndDate.setText(events.getEnd().substring(0, indexEnd));
            fragmentEditEventBinding.editTextEndTime.setText(events.getEnd().substring(indexEnd + 5));
        } else {
            fragmentEditEventBinding.editTextStartDate.setText(events.getStart());
            fragmentEditEventBinding.allDayCheckBox.setChecked(true);
            fragmentEditEventBinding.editTextStartTime.setVisibility(View.GONE);
            fragmentEditEventBinding.editTextEndDate.setVisibility(View.GONE);
            fragmentEditEventBinding.editTextEndTime.setVisibility(View.GONE);
            fragmentEditEventBinding.endTextView.setVisibility(View.GONE);
        }

        fragmentEditEventBinding.editTextStartDate.setOnClickListener(new View.OnClickListener() {
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
                            fragmentEditEventBinding.editTextStartDate.setText(" " + formatter.format(start));
                        }
                    }
                });
            }
        });

        fragmentEditEventBinding.editTextEndDate.setOnClickListener(new View.OnClickListener() {
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
                            fragmentEditEventBinding.editTextEndDate.setText(" " + formatter.format(end));
                        }
                    }
                });
            }
        });

        fragmentEditEventBinding.editTextStartTime.setOnClickListener(new View.OnClickListener() {
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
                        fragmentEditEventBinding.editTextStartTime.setText(startTime);
                    }
                });
            }
        });


        fragmentEditEventBinding.editTextEndTime.setOnClickListener(new View.OnClickListener() {
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
                        fragmentEditEventBinding.editTextEndTime.setText(endTime);
                    }
                });
            }
        });


        //PLACE

        eventsAndPlacesViewModel.getMyPlacesLiveData(false).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                List<Place> fetchedPlaces = result;
                if (!fetchedPlaces.isEmpty()) {
                    PlaceAdapter adapterPlaces = new PlaceAdapter(requireContext(), fetchedPlaces);
                    adapterPlaces.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fragmentEditEventBinding.placesSpinner.setAdapter(adapterPlaces);
                } else {
                    fragmentEditEventBinding.noPersonalPlacesTextView.setVisibility(View.VISIBLE);
                    fragmentEditEventBinding.placesSpinner.setVisibility(View.GONE);
                }
            }
        });

        fragmentEditEventBinding.placeSourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.personalPlacesRadioButton) {
                    //MOSTRA I POSTI CREATI DALL'UTENTE
                    fragmentEditEventBinding.layoutPersonalPlaces.setVisibility(View.VISIBLE);
                    fragmentEditEventBinding.layoutGooglePlaces.setVisibility(View.GONE);
                    googlePlace = false;
                } else {
                    //MOSTRA QUELLI DI GOOGLE PLACES
                    fragmentEditEventBinding.layoutPersonalPlaces.setVisibility(View.GONE);
                    fragmentEditEventBinding.layoutGooglePlaces.setVisibility(View.VISIBLE);
                    googlePlace = true;

                    AutocompleteSupportFragment autocompleteFragment = AutocompleteSupportFragment.newInstance();
                    autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
                    autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS, com.google.android.libraries.places.api.model.Place.Field.PHONE_NUMBER, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME));

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
                            placePhoneNumber = place.getPhoneNumber();
                            photoMetadata = place.getPhotoMetadatas();
                            coordinates = new ArrayList<>();
                            if (place.getLatLng() != null) {
                                coordinates.add(place.getLatLng().latitude);
                                coordinates.add(place.getLatLng().longitude);
                            }
                        }

                        @Override
                        public void onError(String message) {

                        }
                    });
                }
            }
        });


        //ALL DAY CHECKBOX
        fragmentEditEventBinding.allDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fragmentEditEventBinding.editTextStartTime.setVisibility(View.GONE);
                    fragmentEditEventBinding.editTextEndDate.setVisibility(View.GONE);
                    fragmentEditEventBinding.editTextEndTime.setVisibility(View.GONE);
                    fragmentEditEventBinding.endTextView.setVisibility(View.GONE);
                } else {
                    fragmentEditEventBinding.editTextStartTime.setVisibility(View.VISIBLE);
                    fragmentEditEventBinding.editTextEndDate.setVisibility(View.VISIBLE);
                    fragmentEditEventBinding.editTextEndTime.setVisibility(View.VISIBLE);
                    fragmentEditEventBinding.endTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        //PRIVATE
        fragmentEditEventBinding.checkBoxPrivate.setChecked(events.isPrivate());

        //EDIT BUTTON
        fragmentEditEventBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = String.valueOf(fragmentEditEventBinding.editTextTitle.getText());
                description = String.valueOf(fragmentEditEventBinding.editTextDescription.getText());
                startDate = String.valueOf(fragmentEditEventBinding.editTextStartDate.getText());
                endDate = String.valueOf(fragmentEditEventBinding.editTextEndDate.getText());
                startTime = String.valueOf(fragmentEditEventBinding.editTextStartTime.getText());
                endTime = String.valueOf(fragmentEditEventBinding.editTextEndTime.getText());

                boolean isOk = true;
                if (title == null || title.isEmpty()) {
                    fragmentEditEventBinding.editTextTitle.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (description == null || description.isEmpty()) {
                    fragmentEditEventBinding.editTextDescription.setError(getString(R.string.field_mandatory));
                    isOk = false;
                }
                if (fragmentEditEventBinding.allDayCheckBox.isChecked()) {
                    if (startDate == null || startDate.isEmpty()) {
                        fragmentEditEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                } else {
                    if (startDate == null || startDate.isEmpty()) {
                        fragmentEditEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (endDate == null || endDate.isEmpty()) {
                        fragmentEditEventBinding.editTextEndTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (startTime == null || startTime.isEmpty()) {
                        fragmentEditEventBinding.editTextStartTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                    if (endTime == null || endTime.isEmpty()) {
                        fragmentEditEventBinding.editTextEndTime.setError(getString(R.string.field_mandatory));
                        isOk = false;
                    }
                }

                if (address == null || address.isEmpty()) {
                    if (googlePlace) {
                        fragmentEditEventBinding.placeTextView.setError(getString(R.string.field_mandatory));
                    } else {
                        fragmentEditEventBinding.placeTextView1.setError(getString(R.string.field_mandatory));
                    }
                    isOk = true;
                }

                if (fragmentEditEventBinding.checkBoxPrivate.isChecked()) {
                    isPrivate = true;
                } else {
                    isPrivate = false;
                }

                if (isOk) {
                    //MODIFICA EVENTO
                    Events newEvent = new Events();
                    newEvent.setCreatorEmail(userViewModel.getLoggedUser().getEmail());
                    newEvent.setTitle(title);
                    newEvent.setDescription(description);
                    if (fragmentEditEventBinding.allDayCheckBox.isChecked()) {
                        newEvent.setStart(startDate.trim());
                    } else {
                        newEvent.setStart(startDate.trim() + "userH" + startTime);
                        newEvent.setEnd(endDate.trim() + "userH" + endTime);
                    }
                    newEvent.setTimezone("Europe/Rome");
                    if (events.getEventSource() != null && events.getEventSource().getUrlPhoto() != null) {
                        newEvent.setEventSource(new EventSource(null, events.getEventSource().getUrlPhoto()));
                    }
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            newEvent.setEventSource(new EventSource(null, String.valueOf(bitmap)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    newEvent.setCoordinates(coordinates);

                    //SALVATAGGIO CATEGORIA IN INGLESE
                    Resources res = getResources();
                    Configuration conf = res.getConfiguration();
                    conf.locale = Locale.ENGLISH;
                    res.updateConfiguration(conf, null);
                    String[] categoriesEnglish = res.getStringArray(R.array.categories);
                    int selectedCategoryIndex = fragmentEditEventBinding.categoriesSpinner.getSelectedItemPosition();
                    String category = categoriesEnglish[selectedCategoryIndex];
                    newEvent.setCategory(category.toLowerCase(Locale.ROOT));

                    //PLACE
                    if (googlePlace) {
                        List<Place> placeList = new ArrayList<>();
                        Place place = new Place(idPlace, namePlace, "venue", address);
                        place.setIdGoogle(idPlace);
                        place.setCoordinates(coordinates);
                        placeList.add(place);
                        newEvent.setPlaces(placeList);
                        newEvent.setCoordinates(coordinates);
                        if (placePhoneNumber != null) {
                            place.setPhoneNumber(placePhoneNumber);
                        }
                        if (photoMetadata != null) {
                            place.setImages(photoMetadata);
                        }
                    } else { //POSTO CREATO DALL'UTENTE
                        List<Place> placeList = new ArrayList<>();
                        Place selectedPlace = (Place) fragmentEditEventBinding.placesSpinner.getSelectedItem();
                        placeList.add(selectedPlace);
                        newEvent.setPlaces(placeList);
                        newEvent.setCoordinates(selectedPlace.getCoordinates());
                    }

                    newEvent.setPrivate(isPrivate);


                    if (imageUri != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        fragmentEditEventBinding.eventImage.setDrawingCacheEnabled(true);
                        fragmentEditEventBinding.eventImage.buildDrawingCache();
                        Bitmap bitmapImage = ((BitmapDrawable) fragmentEditEventBinding.eventImage.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageReference storageRef = storage.getReference().child("images/" + imageUri + "/");

                        UploadTask uploadTask = storageRef.putBytes(data);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUrl = uri.toString();
                                                Log.i("URL", downloadUrl);
                                                newEvent.setEventSource(new EventSource("user", downloadUrl));
                                                eventsAndPlacesViewModel.editEvent(oldEvent, newEvent);
                                                Navigation.findNavController(requireView()).navigate(R.id.action_editEventFragment_to_containerMyEventsAndPlaces);
                                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                        getString(R.string.event_modified), Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                    } else {
                        eventsAndPlacesViewModel.editEvent(oldEvent, newEvent);
                        Navigation.findNavController(requireView()).navigate(R.id.action_editEventFragment_to_containerMyEventsAndPlaces);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.event_modified), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //CANCEL
        fragmentEditEventBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_editEventFragment_to_containerMyEventsAndPlaces);
            }
        });

        //DELETE EVENT
        fragmentEditEventBinding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsAndPlacesViewModel.deleteMyEvent(events);
                Navigation.findNavController(requireView()).navigate(R.id.action_editEventFragment_to_containerMyEventsAndPlaces);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        getString(R.string.event_deleted), Snackbar.LENGTH_SHORT).show();
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
                    fragmentEditEventBinding.eventImage.setImageURI(imageUri);
                }
            }
        }
    }
}