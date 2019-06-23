package com.example.easycook.Home.Ingredient;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class IngredientForm extends Fragment implements AdapterView.OnItemSelectedListener {

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

        // when done, go back to home fragment
        tickButton = view.findViewById(R.id.tick_button_ingredient);
        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user input and list it in home fragment
                Spinner typeEditText = (Spinner) view.findViewById(R.id.IngredientType_input);
                EditText nameEditText = (EditText) view.findViewById(R.id.IngredientName_input);
                EditText weightEditText = (EditText) view.findViewById(R.id.IngredientWeight_input);
                EditText dateEditText = (EditText) view.findViewById(R.id.IngredientExpiry_input);

                String type = typeEditText.getSelectedItem().toString();
                String name = nameEditText.getText().toString();
                String weight = weightEditText.getText().toString();
                String date = dateEditText.getText().toString();

                // if input is empty, go back to home fragment
                if (TextUtils.isEmpty(type) || TextUtils.isEmpty(name)
                        || TextUtils.isEmpty(weight) || TextUtils.isEmpty(date)) {
                } else {
                    // sort ingredient by type and add into firestore accordingly
                    sortIngredient(type, name, weight, date);
                }
                backToHome(fragmentManager);
            }
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button_ingredient);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        // Create the spinner.
        // set it as listener
        Spinner spinner = view.findViewById(R.id.IngredientType_input);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }

        createSpinner(spinner);

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

    public void sortIngredient(String type, String name, String weight, String date) {
        switch (type) {
            case ("Meat"):
                CollectionReference meatRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_meat");
                meatRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
            case ("Grains"):
                CollectionReference grainsRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_grains");
                grainsRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
            case ("Vegetable"):
                CollectionReference vegRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_vegetable");
                vegRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
            case ("Dairy"):
                CollectionReference dairyRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_dairy");
                dairyRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
            case ("Sauces"):
                CollectionReference saucesRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_sauces");
                saucesRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
            case ("Condiment"):
                CollectionReference condRef = FirebaseFirestore.getInstance()
                        .collection("ingredient_condiment");
                condRef.add(new IngredientItem(type, name, Integer.parseInt(weight), date));
                break;
        }
    }

    public void backToHome(FragmentManager fragmentManager) {
        // replace container view (the main activity container) with ingredient fragment
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, homeFragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    public void createSpinner(Spinner spinner) {
        // Create an ArrayAdapter using the string array and default spinner
        // layout.
        // ArrayAdapter connects array of spinner items to spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                // string array in strings.xml
                R.array.ingredients_array,
                // default Android supplied
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }
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

    // for spinner (date)
    @Override
    public void onItemSelected(AdapterView<?> adapterView,
                               View view, int i, long l) {
        String spinnerLabel = adapterView.getItemAtPosition(i).toString();
    }

    // Interface callback for when no spinner item is selected.
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing.
    }

}