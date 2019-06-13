package com.example.easycook;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.widget.TextView;

// TODO add recycler view
// TODO glade lib
// TODO add button
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        final FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment;

        // bottom navigation bar fragments
        final Fragment homeFragment = new HomeFragment();
        final Fragment exploreFragment = new ExploreFragment();
        final Fragment settingsFragment = new SettingsFragment();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = homeFragment;
                    return true;
                case R.id.navigation_explore:
                    fragment = exploreFragment;
                    return true;
                case R.id.navigation_settings:
                    fragment = settingsFragment;
                    return true;
            }
            fragmentManager.beginTransaction().replace(R.id.nav_view, fragment).commit();
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
        navView.setSelectedItemId(R.id.navigation_home);
    }

}

