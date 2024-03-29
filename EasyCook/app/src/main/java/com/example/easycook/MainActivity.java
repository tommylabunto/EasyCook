package com.example.easycook;

import android.net.Uri;
import android.os.Bundle;

import com.example.easycook.Explore.ExploreFragment;
import com.example.easycook.Home.HomeFragment;
import com.example.easycook.Home.Ingredient.IngredientForm;
import com.example.easycook.Settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements IngredientForm.OnFragmentInteractionListener {

    private final String LOG_TAG = "MainActivity";

    private BottomNavigationView navView;

    private static FirebaseUser user;

    protected static FirebaseAuth mAuth;

    // bottom navigation bar fragments
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currUser != null) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        item.setChecked(true);
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.navigation_explore:
                        item.setChecked(true);
                        selectedFragment = new ExploreFragment();
                        break;
                    case R.id.navigation_settings:
                        item.setChecked(true);
                        selectedFragment = new SettingsFragment();
                        break;
                }
                popGoToFragment(selectedFragment);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpBackButton();

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // if no user, sign in
        if (user == null) {
            goToFragment(new WelcomePageFragment());
            delayNav();
        } else {
            goToFragment(new HomeFragment());
            navView.setVisibility(View.VISIBLE);
        }
    }

    // Inflates the menu, and adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handles app bar item clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            case android.R.id.home:
                getSupportFragmentManager().popBackStackImmediate();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpBackButton() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
                if (stackHeight > 0) { // if we have something on the stack (doesn't include the current shown fragment)
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setHomeButtonEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
    }

    // sign out and then sign in
    private void signOut() {
        if (mAuth != null) {
            mAuth.signOut();
            popGoToFragment(new SignInFragment());
        }
    }

    private void delayNav() {

        // make bottom nav view appear same time as sign in
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                navView.setVisibility(View.VISIBLE);
            }
        };

        handler.postDelayed(runnable, 2500);
    }

    private void goToFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void popGoToFragment(Fragment fragment) {

        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    // used to pass auth from sign in fragment to sign out
    protected static void passAuth(FirebaseAuth auth, FirebaseUser thisUser) {
        mAuth = auth;
        user = thisUser;
    }

    // for debugging
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}

