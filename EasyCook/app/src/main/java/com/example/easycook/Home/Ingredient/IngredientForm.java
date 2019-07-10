package com.example.easycook.Home.Ingredient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.easycook.Explore.ExploreFragment;
import com.example.easycook.Home.HomeFragment;
import com.example.easycook.R;
import com.example.easycook.Settings.ProfileForm;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

public class IngredientForm extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String LOG_TAG = "IngredientForm";

    private EditText date;
    private String selectedDate;

    // for date picker
    // Used to identify the result
    private static final int REQUEST_CODE = 11;
    private OnFragmentInteractionListener mListener;

    // for spinner
    private ArrayAdapter<CharSequence> typeAdapter;
    private ArrayAdapter<CharSequence> unitsAdapter;

    // referenced passed from home fragment
    private IngredientItem ingredient;
    private String id;
    private String path;

    private View view;

    public IngredientForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient_form, container, false);

        this.view = view;

        // inflate toolbar
        setHasOptionsMenu(true);

        // get fragment manager so we can launch from fragment
        final FragmentManager fragmentManager = getFragmentManager();

        // Create the spinner.
        // set it as listener
        Spinner ingredientTypeSpinner = view.findViewById(R.id.IngredientType_input);
        if (ingredientTypeSpinner != null) {
            ingredientTypeSpinner.setOnItemSelectedListener(this);
        }

        createTypeSpinner(ingredientTypeSpinner);

        Spinner ingredientUnitsSpinner = view.findViewById(R.id.IngredientUnits_input);
        if (ingredientUnitsSpinner != null) {
            ingredientUnitsSpinner.setOnItemSelectedListener(this);
        }

        createUnitsSpinner(ingredientUnitsSpinner);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

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
                addIngredient();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    // for ingredient type
    private void createTypeSpinner(Spinner spinner) {
        // Create an ArrayAdapter using the string array and default spinner
        // layout.
        // ArrayAdapter connects array of spinner items to spinner
        typeAdapter = ArrayAdapter.createFromResource(
                getContext(),
                // string array in strings.xml
                R.array.ingredients_array,
                // default Android supplied
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears.
        typeAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(typeAdapter);
        }
    }

    // for ingredient type
    private void createUnitsSpinner(Spinner spinner) {
        // Create an ArrayAdapter using the string array and default spinner
        // layout.
        // ArrayAdapter connects array of spinner items to spinner
        unitsAdapter = ArrayAdapter.createFromResource(
                getContext(),
                // string array in strings.xml
                R.array.units_array,
                // default Android supplied
                android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears.
        unitsAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(unitsAdapter);
        }
    }

    private void checkIfSnapshotExist(final View view) {

        if (path != null) {
            DocumentReference docIdRef = FirebaseFirestore.getInstance().document(path);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Spinner typeEditText = (Spinner) view.findViewById(R.id.IngredientType_input);
                            EditText nameEditText = (EditText) view.findViewById(R.id.IngredientName_input);
                            EditText weightEditText = (EditText) view.findViewById(R.id.IngredientWeight_input);
                            EditText dateEditText = (EditText) view.findViewById(R.id.IngredientExpiry_input);
                            Spinner unitsEditText = (Spinner) view.findViewById(R.id.IngredientUnits_input);

                            if (ingredient != null) {
                                int spinnerPosition = typeAdapter.getPosition(ingredient.getIngredientType());
                                typeEditText.setSelection(spinnerPosition);

                                int spinnerPosition1 = unitsAdapter.getPosition(ingredient.getUnits());
                                unitsEditText.setSelection(spinnerPosition1);
                            }

                            nameEditText.setText(ingredient.getIngredientName());
                            weightEditText.setText("" + ingredient.getWeight());
                            dateEditText.setText(ingredient.getExpiry());

                            Log.d(LOG_TAG, "Document exists!");
                        } else {
                            Log.d(LOG_TAG, "Document does not exist!");
                        }
                    } else {
                        Log.w(LOG_TAG, "Failed with: ", task.getException());
                    }
                }
            });
        }
    }

    private void sortIngredient(IngredientItem ingredient) {

        // keeps track of all ingredients
        // check if user has any ingredients -> generates some for user (so can test features)
        if (id == null) {
            CollectionReference ingredientsRef = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("all_ingredients");
            ingredientsRef.add(ingredient);
        } else {
            CollectionReference meatRef = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("all_ingredients");
            meatRef.document(id).set(ingredient, SetOptions.merge());
        }

        switch (ingredient.getIngredientType()) {
            case ("Meat"):
                if (id == null) {
                    CollectionReference meatRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_meat");
                    meatRef.add(ingredient);
                } else {
                    CollectionReference meatRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_meat");
                    meatRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
            case ("Grains"):
                if (id == null) {
                    CollectionReference grainsRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_grains");
                    grainsRef.add(ingredient);
                } else {
                    CollectionReference grainsRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_grains");
                    grainsRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
            case ("Vegetable"):
                if (id == null) {
                    CollectionReference vegRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_vegetable");
                    vegRef.add(ingredient);
                } else {
                    CollectionReference vegRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_vegetable");
                    vegRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
            case ("Dairy"):
                if (id == null) {
                    CollectionReference dairyRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_dairy");
                    dairyRef.add(ingredient);
                } else {
                    CollectionReference dairyRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_dairy");
                    dairyRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
            case ("Sauces"):
                if (id == null) {
                    CollectionReference saucesRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_sauces");
                    saucesRef.add(ingredient);
                } else {
                    CollectionReference saucesRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_sauces");
                    saucesRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
            case ("Condiment"):
                if (id == null) {
                    CollectionReference condRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_condiment");
                    condRef.add(ingredient);
                } else {
                    CollectionReference condRef = FirebaseFirestore.getInstance()
                            .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_condiment");
                    condRef.document(id).set(ingredient, SetOptions.merge());
                }
                break;
        }
    }

    private void backToHome(FragmentManager fragmentManager) {
        // replace container view (the main activity container) with ingredient fragment
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, homeFragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    // receive reference from ingredientformview fragment
    // need to check if its null (not created before)
    protected void passReference(IngredientItem ingredient, String id, String path) {
        this.ingredient = ingredient;
        this.id = id;
        this.path = path;
    }

    private void addIngredient() {

        // get user input and list it in home fragment
        Spinner typeEditText = (Spinner) view.findViewById(R.id.IngredientType_input);
        EditText nameEditText = (EditText) view.findViewById(R.id.IngredientName_input);
        EditText weightEditText = (EditText) view.findViewById(R.id.IngredientWeight_input);
        EditText dateEditText = (EditText) view.findViewById(R.id.IngredientExpiry_input);
        Spinner unitsEditText = (Spinner) view.findViewById(R.id.IngredientUnits_input);

        String type = typeEditText.getSelectedItem().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String units = unitsEditText.getSelectedItem().toString().trim();

        // if input is empty, go back to home fragment
        if (TextUtils.isEmpty(type) && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(weight) && TextUtils.isEmpty(units)
                && TextUtils.isEmpty(date)) {
        } else {

            if (TextUtils.isEmpty(weight)) {
                weight = "0";
            }

            IngredientItem ingredient = new IngredientItem(type, name, Integer.parseInt(weight), date, 0, units, ProfileForm.user.getUid());

            // pass ingredient to explore fragment to check if its closest to expiring (so it shows up under recommended)
            ExploreFragment.passIngredient(ingredient);
            // sort ingredient by type and add into firestore accordingly
            sortIngredient(ingredient);
        }
        backToHome(getFragmentManager());
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