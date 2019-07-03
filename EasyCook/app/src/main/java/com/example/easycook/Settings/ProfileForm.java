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

    private Button backButton;
    private Button tickButton;

    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ProfileForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_form, container, false);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        // go back to home fragment
        tickButton = view.findViewById(R.id.tick_button_settings);
        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button_settings);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }

    public void checkIfSnapshotExist(final View view) {

        EditText nameEditText = (EditText) view.findViewById(R.id.username_input);
        EditText emailEditText = (EditText) view.findViewById(R.id.email_input);

        nameEditText.setText(user.getDisplayName());
        emailEditText.setText(user.getEmail());
    }

    // saves changes to user data on firebase, not firestore collection
    // takes a while to load changes, done when toast appears
    public void saveUser(String name, String email) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User profile updated.");
                            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "User email address updated.");
                            Toast.makeText(getContext(), "Email address updated", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
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
