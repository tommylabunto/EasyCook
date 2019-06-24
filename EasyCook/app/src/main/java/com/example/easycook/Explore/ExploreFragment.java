package com.example.easycook.Explore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.easycook.Home.Ingredient.IngredientForm;
import com.example.easycook.Home.Recipe.RecipeAdapter;
import com.example.easycook.Home.Recipe.RecipeForm;
import com.example.easycook.Home.Recipe.RecipeItem;
import com.example.easycook.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

// TODO recommended and recipe of the day
public class ExploreFragment extends Fragment {

    private final String LOG_TAG = "ExploreFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recipeRecyclerView;
    private CollectionReference recipeRef;
    private RecipeAdapter recipeAdapter;
    private Query recipeQuery;
    private FirestoreRecyclerOptions<RecipeItem> recipeOptions;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // when click search button
        Button searchButton = view.findViewById(R.id.search_button);
        final EditText searchEditText = view.findViewById(R.id.search_text);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String search = searchEditText.getText().toString().trim();

                hideKeyboard(view);

                // if input is empty, do nothing
                if (TextUtils.isEmpty(search)) {
                } else {
                    updateAdapter(view, search);
                }
            }
        });

        showRecyclerView(view);

        return view;
    }

    public void hideKeyboard(View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showRecyclerView(View view) {
        // recipe
        recipeRef = db.collection("my_recipe");
        recipeQuery = recipeRef.orderBy("name", Query.Direction.ASCENDING);
        recipeOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recipeQuery, RecipeItem.class)
                .build();

        recipeAdapter = new RecipeAdapter(recipeOptions);
        recipeRecyclerView = view.findViewById(R.id.search_recyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeRecyclerView.setAdapter(recipeAdapter);

        recyclerViewSwipe();

        recyclerViewClick();
    }

    // update recycler view when user enter search
    public void updateAdapter(View view, String search) {

        recipeQuery = recipeRef
                .orderBy("name", Query.Direction.ASCENDING)
                .whereArrayContains("ingredient", search);
        recipeOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recipeQuery, RecipeItem.class)
                .build();

        recipeAdapter = new RecipeAdapter(recipeOptions);
        recipeRecyclerView = view.findViewById(R.id.search_recyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeRecyclerView.setAdapter(recipeAdapter);

        // manually call onStart for adapter to start listening
        onStart();

        recyclerViewSwipe();

        recyclerViewClick();
    }

    // delete when swipe left/right
    public void recyclerViewSwipe() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recipeRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                recipeAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recipeRecyclerView);
    }

    // edits value when clicked
    public void recyclerViewClick() {
        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                RecipeItem recipe = documentSnapshot.toObject(RecipeItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                RecipeForm recipeFragment = new RecipeForm();

                // pass reference to ingredient fragment
                recipeFragment.passReference(recipe, id, path);

                goToFragment(recipeFragment);
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
        recipeAdapter.startListening();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        recipeAdapter.stopListening();
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
