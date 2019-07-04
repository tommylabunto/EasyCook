package com.example.easycook.Home.Recipe;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

// when clicked on existing recipe
public class RecipeFormView extends Fragment {

    private final String LOG_TAG = "RecipeFormView";

    private Button editButton;
    private Button backButton;

    // referenced passed from home fragment
    private RecipeItem recipe;
    private String id;
    private String path;

    public RecipeFormView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recipe_form_view, container, false);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        // go back to home fragment
        editButton = view.findViewById(R.id.edit_button_recipe);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // edits recipe
                RecipeForm recipeForm = new RecipeForm();
                recipeForm.passReference(recipe, id, path);
                goToFragment(recipeForm);
            }
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button_recipe_view);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        return view;
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

                            TextView nameEditText = (TextView) view.findViewById(R.id.recipe_name_input_view);
                            TextView ingredientEditText = (TextView) view.findViewById(R.id.ingredient_list_input_view);
                            TextView preparationEditText = (TextView) view.findViewById(R.id.preparation_input_view);

                            nameEditText.setText(recipe.getName());
                            ingredientEditText.setText(recipe.getIngredientString());
                            preparationEditText.setText(recipe.getPreparation());

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
    }

    public void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    public void passReference(RecipeItem recipe, String id, String path) {
        this.recipe = recipe;
        this.id = id;
        this.path = path;
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
