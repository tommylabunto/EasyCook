package com.example.easycook;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;

// TODO if duck, insert under duck
// TODO glade lib
// TODO add add button
// TODO add toolbar (three dots at top right)
public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();

    // bottom navigation bar fragments
    final Fragment homeFragment = new HomeFragment();
    final Fragment exploreFragment = new ExploreFragment();
    final Fragment settingsFragment = new SettingsFragment();
    Fragment active = homeFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    return true;
                case R.id.navigation_explore:
                    fragmentManager.beginTransaction().hide(active).show(exploreFragment).commit();
                    active = exploreFragment;
                    return true;
                case R.id.navigation_settings:
                    fragmentManager.beginTransaction().hide(active).show(settingsFragment).commit();
                    active = settingsFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // set start-up screen to home

        // this is default method
        // navView.setSelectedItemId(R.id.navigation_home);

        // but this prevents fragment from being re-created
        // e.g. user scroll half-way & press something else, can go back to where he was
        fragmentManager.beginTransaction().add(R.id.container, settingsFragment, "settings").hide(settingsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, exploreFragment, "explore").hide(exploreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container, homeFragment, "home").commit();
    }
}

