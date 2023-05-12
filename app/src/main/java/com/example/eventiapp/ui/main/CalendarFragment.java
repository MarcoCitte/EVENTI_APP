package com.example.eventiapp.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventiapp.R;
import com.example.eventiapp.adapter.CalendarAdapter;
import com.example.eventiapp.databinding.FragmentAllEventsBinding;
import com.example.eventiapp.databinding.FragmentCalendarBinding;
import com.example.eventiapp.model.Events;
import com.example.eventiapp.model.EventsResponse;
import com.example.eventiapp.model.Result;
import com.example.eventiapp.repository.events.IRepositoryWithLiveData;
import com.example.eventiapp.util.DateUtils;
import com.example.eventiapp.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private FragmentCalendarBinding fragmentCalendarBinding;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private LayoutInflater inflater;


    private List<Events> eventsList;
    private EventsAndPlacesViewModel eventsAndPlacesViewModel;

    public CalendarFragment() {

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
        eventsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentCalendarBinding = FragmentCalendarBinding.inflate(inflater, container, false);
        return fragmentCalendarBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initWidgets();
        inflater = LayoutInflater.from(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now();
        }

        fragmentCalendarBinding.buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction();
            }
        });

        fragmentCalendarBinding.buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction();
            }
        });

        eventsAndPlacesViewModel.getEventsResponseLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                EventsResponse eventsResponse = ((Result.EventsResponseSuccess) result).getData();
                eventsList = eventsResponse.getEventsList();
                setMonthView();
            }
        });
    }


    private void initWidgets() {
        calendarRecyclerView = fragmentCalendarBinding.calendarRecyclerView;
        monthYearText = fragmentCalendarBinding.monthYearTV;
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            calendarAdapter = new CalendarAdapter(daysInMonth, String.valueOf(selectedDate.getMonthValue()), eventsList, this);
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ArrayList<String> daysInMonthArray = new ArrayList<>();
            YearMonth yearMonth = null;

            yearMonth = YearMonth.from(date);

            int daysInMonth = 0;
            daysInMonth = yearMonth.lengthOfMonth();
            LocalDate firstOfMonth = null;
            firstOfMonth = selectedDate.withDayOfMonth(1);

            int dayOfWeek = 0;
            dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

            for (int i = 1; i <= 42; i++) {
                if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                    daysInMonthArray.add("");
                } else {
                    daysInMonthArray.add(String.valueOf(i - dayOfWeek));
                }
            }
            return daysInMonthArray;
        }
        return null;
    }

    private String monthYearFromDate(LocalDate date) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return date.format(formatter);
        }
        return null;
    }

    public void previousMonthAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = selectedDate.minusMonths(1);
        }
        setMonthView();
    }

    public void nextMonthAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = selectedDate.plusMonths(1);
        }
        setMonthView();
    }


    @Override
    public void onItemClick(int position, String dayText, int isThereEvent) {
        if (isThereEvent == 0 && !dayText.equals("")) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            String dateString = dayText + " " + monthYearFromDate(selectedDate);
            Date date = DateUtils.parseDate(dateString, "en");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            if (date != null) {
                String formattedDate = formatter.format(date);
                bundle.putString("date", formattedDate);
                Navigation.findNavController(requireView()).navigate(R.id.action_containerEventsPlacesCalendar_to_eventsInADateFragment, bundle);
            }
        }else{
            String message = "No events in this date";
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
