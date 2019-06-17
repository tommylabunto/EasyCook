package com.example.easycook;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.ViewGroup;

// TODO glade lib
public class MainActivity extends AppCompatActivity {

    final FragmentManager fragmentManager = getSupportFragmentManager();

    // bottom navigation bar fragments
    final Fragment homeFragment = new HomeFragment();
    final Fragment exploreFragment = new ExploreFragment();
    final Fragment settingsFragment = new SettingsFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager.beginTransaction().replace(R.id.container,homeFragment).commit();
                    return true;
                case R.id.navigation_explore:
                    fragmentManager.beginTransaction().replace(R.id.container,exploreFragment).commit();
                    return true;
                case R.id.navigation_settings:
                    fragmentManager.beginTransaction().replace(R.id.container,settingsFragment).commit();
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
        navView.setSelectedItemId(R.id.navigation_home);
    }
}

