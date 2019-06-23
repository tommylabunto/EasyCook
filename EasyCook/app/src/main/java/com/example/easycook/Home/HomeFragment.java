package com.example.easycook.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.easycook.Home.Ingredient.IngredientAdapter;
import com.example.easycook.Home.Ingredient.IngredientForm;
import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.Home.Recipe.RecipeAdapter;
import com.example.easycook.Home.Recipe.RecipeForm;
import com.example.easycook.Home.Recipe.RecipeItem;
import com.example.easycook.MainActivity;
import com.example.easycook.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

// TODO add background color for item & make smaller
// TODO cannot see text when typing
/**
 * Home page
 * Entire page is a scroll view and individual categories are recycler views
 */
public class HomeFragment extends Fragment {

    private final String LOG_TAG = "HomeFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // to show recycler view
    private RecyclerView meatRecyclerView;
    private RecyclerView grainsRecyclerView;
    private RecyclerView vegRecyclerView;
    private RecyclerView dairyRecyclerView;
    private RecyclerView saucesRecyclerView;
    private RecyclerView condRecyclerView;
    private RecyclerView recipeRecyclerView;

    private CollectionReference meatRef;
    private CollectionReference grainsRef;
    private CollectionReference vegRef;
    private CollectionReference dairyRef;
    private CollectionReference saucesRef;
    private CollectionReference condRef;
    private CollectionReference recipeRef;

    private IngredientAdapter meatAdapter;
    private IngredientAdapter grainsAdapter;
    private IngredientAdapter vegAdapter;
    private IngredientAdapter dairyAdapter;
    private IngredientAdapter saucesAdapter;
    private IngredientAdapter condAdapter;
    private RecipeAdapter recipeAdapter;

    private Query meatQuery;
    private Query grainsQuery;
    private Query vegQuery;
    private Query dairyQuery;
    private Query saucesQuery;
    private Query condQuery;
    private Query recipeQuery;

    private FirestoreRecyclerOptions<IngredientItem> meatOptions;
    private FirestoreRecyclerOptions<IngredientItem> grainsOptions;
    private FirestoreRecyclerOptions<IngredientItem> vegOptions;
    private FirestoreRecyclerOptions<IngredientItem> dairyOptions;
    private FirestoreRecyclerOptions<IngredientItem> saucesOptions;
    private FirestoreRecyclerOptions<IngredientItem> condOptions;
    private FirestoreRecyclerOptions<RecipeItem> recipeOptions;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // get fragment manager so we can launch from fragment
        final FragmentManager fragmentManager = getFragmentManager();

        // when click ingredient button
        Button ingredientButton = view.findViewById(R.id.ingredientButton);
        ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();
                goToFragment(ingredientFragment);
            }
        });

        // when click recipe button
        Button recipeButton = view.findViewById(R.id.recipeButton);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // replace container view (the main activity container) with recipe fragment
                RecipeForm recipeForm = new RecipeForm();
                goToFragment(recipeForm);
            }
        });

        showRecyclerView(view);

        // this causes the error of recycler view only appearing
        // if click on ingredient form (but don't fill in anything)
        //meatRecyclerView.setHasFixedSize(true);

        return view;
    }

    public void showRecyclerView(View view) {

        // meat
        meatRef = db.collection("ingredient_meat");
        meatQuery = meatRef;
        meatOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(meatQuery, IngredientItem.class)
                .build();

        meatAdapter = new IngredientAdapter(meatOptions);
        meatRecyclerView = view.findViewById(R.id.meat_recyclerView);
        meatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        meatRecyclerView.setAdapter(meatAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView meatRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                meatAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(meatRecyclerView);

        // edits value when clicked
        meatAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // grains
        grainsRef = db.collection("ingredient_grains");
        grainsQuery = grainsRef;
        grainsOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(grainsQuery, IngredientItem.class)
                .build();

        grainsAdapter = new IngredientAdapter(grainsOptions);
        grainsRecyclerView = view.findViewById(R.id.grains_recyclerView);
        grainsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        grainsRecyclerView.setAdapter(grainsAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView grainsRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                grainsAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(grainsRecyclerView);

        // edits value when clicked
        grainsAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // veg
        vegRef = db.collection("ingredient_vegetable");
        vegQuery = vegRef;
        vegOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(vegQuery, IngredientItem.class)
                .build();

        vegAdapter = new IngredientAdapter(vegOptions);
        vegRecyclerView = view.findViewById(R.id.veg_recyclerView);
        vegRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vegRecyclerView.setAdapter(vegAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView vegRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                vegAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(vegRecyclerView);

        // edits value when clicked
        vegAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // dairy
        dairyRef = db.collection("ingredient_dairy");
        dairyQuery = dairyRef;
        dairyOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(dairyQuery, IngredientItem.class)
                .build();

        dairyAdapter = new IngredientAdapter(dairyOptions);
        dairyRecyclerView = view.findViewById(R.id.dairy_recyclerView);
        dairyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dairyRecyclerView.setAdapter(dairyAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView dairyRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                dairyAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(dairyRecyclerView);

        // edits value when clicked
        dairyAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // sauces
        saucesRef = db.collection("ingredient_sauces");
        saucesQuery = saucesRef;
        saucesOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(saucesQuery, IngredientItem.class)
                .build();

        saucesAdapter = new IngredientAdapter(saucesOptions);
        saucesRecyclerView = view.findViewById(R.id.sauces_recyclerView);
        saucesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        saucesRecyclerView.setAdapter(saucesAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView saucesRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                saucesAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(saucesRecyclerView);

        // edits value when clicked
        saucesAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // condiment
        condRef = db.collection("ingredient_condiment");
        condQuery = condRef;
        condOptions = new FirestoreRecyclerOptions.Builder<IngredientItem>()
                .setQuery(condQuery, IngredientItem.class)
                .build();

        condAdapter = new IngredientAdapter(condOptions);
        condRecyclerView = view.findViewById(R.id.cond_recyclerView);
        condRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        condRecyclerView.setAdapter(condAdapter);

        // delete when swipe left/right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView condRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                condAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(condRecyclerView);

        // edits value when clicked
        condAdapter.setOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                IngredientItem ingredient = documentSnapshot.toObject(IngredientItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                // replace container view (the main activity container) with ingredient fragment
                IngredientForm ingredientFragment = new IngredientForm();

                // pass reference to ingredient fragment
                ingredientFragment.passReference(ingredient, id, path);

                goToFragment(ingredientFragment);
            }
        });

        // recipe
        recipeRef = db.collection("my_recipe");
        recipeQuery = recipeRef;
        recipeOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recipeQuery, RecipeItem.class)
                .build();

        recipeAdapter = new RecipeAdapter(recipeOptions);
        recipeRecyclerView = view.findViewById(R.id.recipe_recyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeRecyclerView.setAdapter(recipeAdapter);

        // delete when swipe left/right
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

        // edits value when clicked
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
        meatAdapter.startListening();
        grainsAdapter.startListening();
        vegAdapter.startListening();
        dairyAdapter.startListening();
        saucesAdapter.startListening();
        condAdapter.startListening();
        recipeAdapter.startListening();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        meatAdapter.stopListening();
        grainsAdapter.stopListening();
        vegAdapter.stopListening();
        dairyAdapter.stopListening();
        saucesAdapter.stopListening();
        condAdapter.stopListening();
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
