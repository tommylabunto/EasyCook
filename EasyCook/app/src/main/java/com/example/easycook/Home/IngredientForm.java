package com.example.easycook.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.easycook.R;

// TODO replace type of ingredient from string input to spinner
public class IngredientForm extends Fragment {

    private Button tickButton;
    private Button backButton;
    private EditText date;
    private String selectedDate;

    private final String LOG_TAG = "IngredientForm";
    // Used to identify the result
    public static final int REQUEST_CODE = 11;
    private OnFragmentInteractionListener mListener;

    public IngredientForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient_form, container, false);

        // get fragment manager so we can launch from fragment
        final FragmentManager fragmentManager = getFragmentManager();

        // go back to home fragment
        tickButton = view.findViewById(R.id.tick_button);
        tickButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // replace container view (the main activity container) with ingredient fragment
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, homeFragment);

                // add to back stack so user can navigate back
                transaction.addToBackStack(null);

                // make changes
                transaction.commit();

                // get user input and list it in home fragment
                EditText typeEditText = (EditText) view.findViewById(R.id.IngredientType_input);
                EditText nameEditText = (EditText) view.findViewById(R.id.IngredientName_input);
                EditText weightEditText = (EditText) view.findViewById(R.id.IngredientWeight_input);
                EditText dateEditText = (EditText) view.findViewById(R.id.IngredientExpiry_input);

                String type = typeEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String weight = weightEditText.getText().toString();
                String date = dateEditText.getText().toString();

                // if input is empty, go back to home fragment
                if (TextUtils.isEmpty(type) || TextUtils.isEmpty(name)
                        || TextUtils.isEmpty(weight) || TextUtils.isEmpty(date)) {
                    // when fill in ingredient form, it attaches
                    // and pauses home fragment (this is on top)
                    fragmentManager.popBackStackImmediate();
                    fragmentManager.popBackStackImmediate();
                } else {
                    homeFragment.createNewIngredient(type, name, Integer.parseInt(weight), date);
                }
            }
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });
        // Creates a new date picker fragment and show it.
        date = view.findViewById(R.id.IngredientExpiry_input);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(IngredientForm.this, REQUEST_CODE);
                newFragment.show(fragmentManager,
                        getString(R.string.datepicker));
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            selectedDate = data.getStringExtra("selectedDate");
            // set the value of the editText
            date.setText(selectedDate);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(LOG_TAG, "onDetach");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}