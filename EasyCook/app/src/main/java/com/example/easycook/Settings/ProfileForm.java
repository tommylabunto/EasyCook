package com.example.easycook.Settings;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;

public class ProfileForm extends Fragment {

    private String LOG_TAG = "ProfileForm";

    private View view;

    // other classes access it to access ingredients/recipe on firestore
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ProfileForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_form, container, false);

        this.view = view;

        // inflate toolbar
        setHasOptionsMenu(true);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        return view;
    }

    // Inflates the menu, and adds items to the action bar if it is present.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_tick, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handles app bar item clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tick_button:
                saveChanges();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIfSnapshotExist(final View view) {

        EditText nameEditText = (EditText) view.findViewById(R.id.username_input);
        EditText emailEditText = (EditText) view.findViewById(R.id.email_input);

        nameEditText.setText(user.getDisplayName());
        emailEditText.setText(user.getEmail());
    }

    private void saveChanges() {
        // save changes in firestore documents
        EditText nameEditText = (EditText) view.findViewById(R.id.username_input);
        EditText emailEditText = (EditText) view.findViewById(R.id.email_input);

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // if input is empty, go back to home fragment
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
        } else {
            // save into firestore
            saveUser(name, email);
        }
        goToFragment(new SettingsFragment());
    }

    // saves changes to user data on firebase, not firestore collection
    // only save changes when input is different
    // takes a while to load changes, done when toast appears
    private void saveUser(String name, String email) {

        if (!name.equals(user.getDisplayName())) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "User profile updated.");
                                showToast("Profile updated");
                            }
                        }
                    });
        } else {
            Log.d(LOG_TAG, "name not updated");
        }

        if (!email.equals(user.getEmail())) {
            FirebaseAuth.getInstance().getCurrentUser().updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(LOG_TAG, "User email address updated.");
                                showToast("Email address updated");
                            }
                        }
                    });
        } else {
            Log.d(LOG_TAG, "email not updated");
        }
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    private void showToast(String msg) {

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    // for debugging
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "onDetach");
    }

}
