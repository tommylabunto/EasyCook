package com.example.easycook.Explore;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.easycook.Home.Ingredient.IngredientForm;
import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.Home.Recipe.RecipeAdapter;
import com.example.easycook.Home.Recipe.RecipeForm;
import com.example.easycook.Home.Recipe.RecipeFormView;
import com.example.easycook.Home.Recipe.RecipeItem;
import com.example.easycook.R;
import com.example.easycook.Settings.ProfileForm;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Random;

// Search recipe by typing ingredient (caps and leave a space)
public class ExploreFragment extends Fragment {

    private final String LOG_TAG = "ExploreFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recipeRecyclerView;
    private RecyclerView recommendedRecyclerView;
    private RecyclerView recentRecyclerView;
    private RecyclerView todayRecyclerView;

    private CollectionReference recipeRef;
    private CollectionReference recommendedRef;
    private CollectionReference recentRef;
    private CollectionReference todayRef;

    private RecipeAdapter recipeAdapter;
    private RecipeAdapter recommendedAdapter;
    private RecipeAdapter recentAdapter;
    private RecipeAdapter todayAdapter;

    private Query recipeQuery;
    private Query recommendedQuery;
    private Query recentQuery;
    private Query todayQuery;

    private FirestoreRecyclerOptions<RecipeItem> recipeOptions;
    private FirestoreRecyclerOptions<RecipeItem> recommendedOptions;
    private FirestoreRecyclerOptions<RecipeItem> recentOptions;
    private FirestoreRecyclerOptions<RecipeItem> todayOptions;

    // keeps track of recent recipes clicked
    // thus recycler view can only show 1
    public static String recentDocumentID;

    // the ingredient closest to expiring
    public static IngredientItem ingredient;

    private String id;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // when click search button
        ImageView searchButton = view.findViewById(R.id.search_button);
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

        showRecipeRecyclerView(view);
        showRecommendedRecyclerView(view);
        showRecentRecyclerView(view);
        showTodayRecyclerView(view);

        return view;
    }

    public void hideKeyboard(View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Log.i(LOG_TAG, "hideKeyboard");
        }
    }

    public void showRecipeRecyclerView(View view) {
        // recipe
        recipeRef = db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
        recipeQuery = recipeRef.orderBy("name", Query.Direction.ASCENDING);
        recipeOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recipeQuery, RecipeItem.class)
                .build();

        recipeAdapter = new RecipeAdapter(recipeOptions);
        recipeRecyclerView = view.findViewById(R.id.search_recyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeRecyclerView.setAdapter(recipeAdapter);

        recyclerViewSwipe(recipeAdapter, recipeRecyclerView);

        recyclerViewClick(recipeAdapter);
    }

    // recommends only recipes with the ingredient closest to expiry
    public void showRecommendedRecyclerView(View view) {

        if (ingredient == null) {
            ingredient = new IngredientItem("", "", 0, "", 0, "");
        }
        // recommended
        recommendedRef = db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
        recommendedQuery = recommendedRef
                .orderBy("name", Query.Direction.ASCENDING)
                .whereArrayContains("ingredient", ingredient.getIngredientName());
        recommendedOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recommendedQuery, RecipeItem.class)
                .build();

        recommendedAdapter = new RecipeAdapter(recommendedOptions);
        recommendedRecyclerView = view.findViewById(R.id.recommended_recyclerView);
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recommendedRecyclerView.setAdapter(recommendedAdapter);

        recyclerViewSwipe(recommendedAdapter, recommendedRecyclerView);

        recyclerViewClick(recommendedAdapter);
    }

    // show recent
    // can only show one
    public void showRecentRecyclerView(View view) {
        recentRef = db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
        recentQuery = recentRef
                .orderBy("name", Query.Direction.ASCENDING)
                .whereEqualTo("documentID", recentDocumentID);
        recentOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(recentQuery, RecipeItem.class)
                .build();

        recentAdapter = new RecipeAdapter(recentOptions);
        recentRecyclerView = view.findViewById(R.id.recent_recyclerView);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentRecyclerView.setAdapter(recentAdapter);

        recyclerViewSwipe(recentAdapter, recentRecyclerView);

        recyclerViewClick(recentAdapter);
    }

    // show recipe of the day
    public void showTodayRecyclerView(View view) {

        // the cheat way
        // refreshes every time start app
        String[] ingredientList = {"1 tablespoon butter", "3 eggs", "1 teaspoon vanilla", "1 cup white sugar", "1/2 cup Milk"};
        Random random = new Random();
        String search = ingredientList[random.nextInt(ingredientList.length)];

        todayRef = db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
        todayQuery = todayRef
                .orderBy("name", Query.Direction.ASCENDING)
                .whereArrayContains("ingredient", search);
        todayOptions = new FirestoreRecyclerOptions.Builder<RecipeItem>()
                .setQuery(todayQuery, RecipeItem.class)
                .build();

        todayAdapter = new RecipeAdapter(todayOptions);
        todayRecyclerView = view.findViewById(R.id.today_recyclerView);
        todayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todayRecyclerView.setAdapter(todayAdapter);

        recyclerViewSwipe(todayAdapter, todayRecyclerView);

        recyclerViewClick(todayAdapter);
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

        recyclerViewSwipe(recipeAdapter, recipeRecyclerView);

        recyclerViewClick(recipeAdapter);
    }

    // delete when swipe left/right
    public void recyclerViewSwipe(final RecipeAdapter adapter, RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recipeRecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    // edits value when clicked
    public void recyclerViewClick(RecipeAdapter adapter) {
        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                RecipeItem recipe = documentSnapshot.toObject(RecipeItem.class);
                id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                recentDocumentID = id;

                // replace container view (the main activity container) with ingredient fragment
                RecipeFormView recipeFragmentView = new RecipeFormView();

                // pass reference to ingredient fragment
                recipeFragmentView.passReference(recipe, id, path);

                goToFragment(recipeFragmentView);
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

    public static void passIngredient(IngredientItem thisIngredient) {

        if ((ingredient == null && thisIngredient.getNumDays() >= 0) ||
                (thisIngredient.getNumDays() < ingredient.getNumDays() && thisIngredient.getNumDays() > 0)) {
            ingredient = thisIngredient;
        }
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

        setID();

        recipeAdapter.startListening();
        recommendedAdapter.startListening();
        recentAdapter.startListening();
        todayAdapter.startListening();
        Log.d(LOG_TAG, "onStart");
    }

    // set document id for each recipe
    // cheat way
    public void setID() {
        recipeRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            RecipeItem recipe = documentSnapshot.toObject(RecipeItem.class);

                            if (recipe.getDocumentID() == null) {
                                // set document id
                                FirebaseFirestore.getInstance()
                                        .collection("users").document(ProfileForm.user.getUid())
                                        .collection("my_recipe").document(documentSnapshot.getId())
                                        .set(new RecipeItem(recipe.getName(), recipe.getIngredient(),
                                                recipe.getPreparation(), documentSnapshot.getId(),
                                                recipe.getUrl(), recipe.getImageLink(), recipe.getPath()), SetOptions.merge());
                            } else {
                                Log.d(LOG_TAG, "recipe is null");
                            }
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        recipeAdapter.stopListening();
        recommendedAdapter.stopListening();
        recentAdapter.stopListening();
        todayAdapter.stopListening();
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
