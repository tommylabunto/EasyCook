package com.example.easycook.Home.Ingredient;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.easycook.Explore.ExploreFragment;
import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// when clicked on existing ingredient on home fragment
public class IngredientFormView extends Fragment {

    private final String LOG_TAG = "IngredientFormView";

    // referenced passed from home fragment
    private IngredientItem ingredient;
    private String id;
    private String path;

    private View view;

    public IngredientFormView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient_form_view, container, false);

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
        getActivity().getMenuInflater().inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handles app bar item clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_button:
                editIngredient();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkIfSnapshotExist(final View view) {

        if (path != null) {
            DocumentReference docIdRef = FirebaseFirestore.getInstance().document(path);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            TextView typeEditText = (TextView) view.findViewById(R.id.IngredientType_input_view);
                            TextView nameEditText = (TextView) view.findViewById(R.id.IngredientName_input_view);
                            TextView weightEditText = (TextView) view.findViewById(R.id.IngredientWeight_input_view);
                            TextView dateEditText = (TextView) view.findViewById(R.id.IngredientExpiry_input_view);
                            TextView unitsEditText = (TextView) view.findViewById(R.id.IngredientUnits_input_view);

                            typeEditText.setText(ingredient.getIngredientType());
                            nameEditText.setText(ingredient.getIngredientName());
                            weightEditText.setText("" + ingredient.getWeight());
                            dateEditText.setText(ingredient.getExpiry());
                            unitsEditText.setText(ingredient.getUnits());

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

    public void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    // receive reference from home fragment
    // need to check if its null (not created before)
    public void passReference(IngredientItem ingredient, String id, String path) {
        this.ingredient = ingredient;
        this.id = id;
        this.path = path;
    }

    private void editIngredient() {
        // pass reference to form
        IngredientForm ingredientForm = new IngredientForm();
        ingredientForm.passReference(ingredient, id, path);

        // edit form
        goToFragment(ingredientForm);
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
        Log.d(LOG_TAG, "onStart");
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
