package com.example.easycook.Home.Recipe;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.R;


public class RecipeForm extends Fragment {

    private final String LOG_TAG = "RecipeForm";
    private Button backButton;
    private Button tickButton;

    public RecipeForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recipe_form, container, false);

        // get fragment manager so we can launch from fragment
        final FragmentManager fragmentManager = getFragmentManager();

        // go back to home fragment
        tickButton = view.findViewById(R.id.tick_button_recipe);
        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // replace container view (the main activity container) with home fragment
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, homeFragment);

                // add to back stack so user can navigate back
                transaction.addToBackStack(null);

                // make changes
                transaction.commit();

                // get user input and list it in home fragment
                EditText ingredientEditText = (EditText) view.findViewById(R.id.ingredient_list_input);
                EditText preparationEditText = (EditText) view.findViewById(R.id.preparation_input);

                String ingredient = ingredientEditText.getText().toString();
                String preparation = preparationEditText.getText().toString();

                homeFragment.createNewRecipe(ingredient, preparation);
            }
        });

        // pressing back doesn't save any changes
        backButton = view.findViewById(R.id.back_button_recipe);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStackImmediate();
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
