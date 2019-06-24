package com.example.easycook.Home.Recipe;


import android.content.Context;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.List;


public class RecipeForm extends Fragment {

    private final String LOG_TAG = "RecipeForm";
    private Button backButton;
    private Button tickButton;

    // referenced passed from home fragment
    private RecipeItem recipe;
    private String id;
    private String path;

    public RecipeForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recipe_form, container, false);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        // get fragment manager so we can launch from fragment
        final FragmentManager fragmentManager = getFragmentManager();

        // go back to home fragment
        tickButton = view.findViewById(R.id.tick_button_recipe);
        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user input and list it in home fragment
                EditText nameEditText = (EditText) view.findViewById(R.id.recipe_name_input);
                EditText ingredientEditText = (EditText) view.findViewById(R.id.ingredient_list_input);
                EditText preparationEditText = (EditText) view.findViewById(R.id.preparation_input);

                String name = nameEditText.getText().toString().trim();
                String ingredient = ingredientEditText.getText().toString().trim();
                String preparation = preparationEditText.getText().toString().trim();



                // if input is empty, go back to home fragment
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ingredient)
                        || TextUtils.isEmpty(preparation)) {
                } else {
                    // add into firestore
                    addRecipe(name, Arrays.asList(ingredient.split(" ")), preparation);
                }
                backToHome(fragmentManager);
            }
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button_recipe);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                            EditText nameEditText = (EditText) view.findViewById(R.id.recipe_name_input);
                            EditText ingredientEditText = (EditText) view.findViewById(R.id.ingredient_list_input);
                            EditText preparationEditText = (EditText) view.findViewById(R.id.preparation_input);

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

    public void passReference(RecipeItem recipe, String id, String path) {
        this.recipe = recipe;
        this.id = id;
        this.path = path;
    }

    public void addRecipe(String name, List<String> ingredient, String preparation) {

        if (id == null) {
            CollectionReference myRecipe = FirebaseFirestore.getInstance()
                    .collection("my_recipe");
            myRecipe.add(new RecipeItem(name, ingredient, preparation));
        } else {
            CollectionReference myRecipe = FirebaseFirestore.getInstance()
                    .collection("my_recipe");
            myRecipe.document(id).set(new RecipeItem(name, ingredient, preparation), SetOptions.merge());
        }
    }

    public void backToHome(FragmentManager fragmentManager) {
        // replace container view (the main activity container) with home fragment
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, homeFragment);

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
