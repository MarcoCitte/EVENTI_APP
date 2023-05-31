package com.example.eventiapp.ui.main;

import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static com.example.eventiapp.util.Constants.SHARED_PREFERENCES_LANGUAGE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.eventiapp.R;
import com.example.eventiapp.util.LanguageUtil;
import com.example.eventiapp.util.SharedPreferencesUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(getApplication());
        String language = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_LANGUAGE);
        //LanguageUtil.setAppLanguage(this, language);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

    }
}