package com.example.easycook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// TODO replace weight input from string to int (change ingredientitem also)
// TODO replace type of ingredient from string input to spinner
// TODO replace expiry date from string input to date picker
// TODO add hint for input fields
public class IngredientForm extends Fragment {

    private final String LOG_TAG = "IngredientForm";

    public IngredientForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient_form, container, false);

        Button tickButton = view.findViewById(R.id.tick_button);
        tickButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // replace container view with ingredient fragment
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.form_container,homeFragment);

                // add to back stack so user can navigate back
                transaction.addToBackStack(null);

                // make changes
                transaction.commit();
            }
        });
        return view;
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