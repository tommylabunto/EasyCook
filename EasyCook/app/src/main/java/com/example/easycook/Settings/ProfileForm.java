package com.example.easycook.Settings;


import android.content.Context;
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

import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    public static ProfileItem user;

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
                EditText phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber_input);

                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

                // if input is empty, go back to home fragment
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(phoneNumber)) {
                } else {
                    // save into firestore
                    saveUser(name, email, phoneNumber);
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

        DocumentReference docIdRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        EditText nameEditText = (EditText) view.findViewById(R.id.username_input);
                        EditText emailEditText = (EditText) view.findViewById(R.id.email_input);
                        EditText phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber_input);

                        nameEditText.setText(user.getUsername());
                        emailEditText.setText(user.getEmail());
                        phoneNumberEditText.setText("" + user.getPhoneNumber());

                        Log.d(LOG_TAG, "Document exists!");
                    } else {
                        Log.d(LOG_TAG, "Document does not exist!");
                    }
                } else {
                    Log.d(LOG_TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    public void saveUser(String name, String email, String phoneNumber) {

        user = new ProfileItem(user.getUid(), name, email, Integer.parseInt(phoneNumber));

        // save document in firestore
        CollectionReference myUsers = FirebaseFirestore.getInstance()
                .collection("users");
        myUsers.document(user.getUid())
                .set(user, SetOptions.merge());
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    public static void passUser(ProfileItem thisUser) {
        user = thisUser;
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
