package com.example.eventiapp.ui.user;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventiapp.R;
import com.example.eventiapp.databinding.FragmentAccountBinding;
import com.example.eventiapp.util.Constants;
import com.example.eventiapp.util.LanguageUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding fragmentAccountBinding;
    private String[] languages;
    private SharedPreferencesUtil sharedPreferencesUtil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                LanguageUtil.setAppLanguage(requireContext(), selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
