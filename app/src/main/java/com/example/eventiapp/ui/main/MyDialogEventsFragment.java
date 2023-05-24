package com.example.eventiapp.ui.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentDialogBinding;
import com.example.eventiapp.util.DateUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MyDialogEventsFragment extends DialogFragment implements View.OnClickListener {

    private FragmentDialogBinding fragmentDialogBinding;
    private static final String TAG = MyDialogEventsFragment.class.getSimpleName();

    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
    private MyDialogListener listener;
    private List<String> allCategories;
    private List<String> checkedCategories = new ArrayList<>();
    private String startDate;
    private String endDate;

    @Override
    public void onClick(View v) {

    }

    public interface MyDialogListener {
        void onFilterApply(List<String> checkedCategories, String startDate, String endDate);
    }

    public MyDialogEventsFragment(List<String> allCategories) {
        this.allCategories = allCategories;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment fragment = getParentFragment();
        if (fragment instanceof MyDialogListener) {
            try {
                listener = (MyDialogListener) fragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement FilterDialogListener");
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentDialogBinding = FragmentDialogBinding.inflate(inflater, container, false);
        return fragmentDialogBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setChips(allCategories);
        Bundle bundle = getArguments();
        if (bundle != null) {
            startDate = bundle.getString("fromDate");
            endDate = bundle.getString("toDate");
            if(!Objects.equals(startDate, "") && !Objects.equals(endDate, "")) {
                fragmentDialogBinding.fromTV.setText(startDate);
                fragmentDialogBinding.toTV.setText(endDate);
                fragmentDialogBinding.fromTV0.setVisibility(View.VISIBLE);
                fragmentDialogBinding.toTV0.setVisibility(View.VISIBLE);
                fragmentDialogBinding.fromTV.setVisibility(View.VISIBLE);
                fragmentDialogBinding.toTV.setVisibility(View.VISIBLE);
            }
        }
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");
        materialDatePicker = builder.build();

        fragmentDialogBinding.dateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getParentFragmentManager(), TAG);
                // Implementazione dei listener per gestire la selezione
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        Date start = new Date(selection.first);
                        Date end = new Date(selection.second);
                        start = DateUtils.parseDate(String.valueOf(start), "EN");
                        end = DateUtils.parseDate(String.valueOf(end), "EN");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (start != null || end != null) {
                            fragmentDialogBinding.fromTV0.setVisibility(View.VISIBLE);
                            fragmentDialogBinding.toTV0.setVisibility(View.VISIBLE);
                            fragmentDialogBinding.fromTV.setVisibility(View.VISIBLE);
                            fragmentDialogBinding.toTV.setVisibility(View.VISIBLE);

                            fragmentDialogBinding.fromTV.setText(" " + formatter.format(start));
                            fragmentDialogBinding.toTV.setText(" " + formatter.format(end));
                            startDate = String.valueOf(fragmentDialogBinding.fromTV.getText());
                            endDate = String.valueOf(fragmentDialogBinding.toTV.getText());
                        }
                    }
                });

                materialDatePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        // Azione da eseguire se l'utente annulla la selezione
                    }
                });
            }
        });

        fragmentDialogBinding.applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get filter parameters
                String fromDate = fragmentDialogBinding.fromTV.getText().toString().trim();
                String toDate = fragmentDialogBinding.toTV.getText().toString().trim();

                // Notify listener of filter application
                listener.onFilterApply(checkedCategories, fromDate, toDate);
                dismiss();
            }
        });

        fragmentDialogBinding.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        fragmentDialogBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFilterApply(Collections.emptyList(),"","");
                dismiss();
            }
        });

    }


    private void setChips(List<String> allCategories) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            checkedCategories = bundle.getStringArrayList("categories");
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(16);
        for (int i = 0; i < allCategories.size(); i++) {
            Chip chip = new Chip(getContext());
            chip.setText(allCategories.get(i));
            chip.setChipBackgroundColorResource(R.color.colorBackgroundSecondary);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setCloseIconVisible(false);
            if (checkedCategories != null && !checkedCategories.isEmpty()) {
                if (checkedCategories.contains(allCategories.get(i))) {
                    chip.setChecked(true);
                    chip.setChipBackgroundColorResource(R.color.colorBackground);
                }
            }else{
                checkedCategories=new ArrayList<>();
            }
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedCategories.add((String) buttonView.getText());
                        chip.setChipBackgroundColorResource(R.color.colorBackground);
                    } else {
                        checkedCategories.remove(buttonView.getText());
                        chip.setChipBackgroundColorResource(R.color.colorBackgroundSecondary);
                    }
                }
            });

            fragmentDialogBinding.chipsLinearLayoutDialog.addView(chip, params);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}