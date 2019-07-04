package com.example.easycook.Home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.easycook.Explore.ExploreFragment;
import com.example.easycook.Home.Ingredient.IngredientAdapter;
import com.example.easycook.Home.Ingredient.IngredientForm;
import com.example.easycook.Home.Ingredient.IngredientFormView;
import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.Home.Recipe.RecipeAdapter;
import com.example.easycook.Home.Recipe.RecipeForm;
import com.example.easycook.Home.Recipe.RecipeFormView;
import com.example.easycook.Home.Recipe.RecipeItem;
import com.example.easycook.R;
import com.example.easycook.Settings.ProfileForm;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// use random email generator to register for food2fork
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

    // to display database recipes
    private static final String API_URL_SEARCH_BASE = "https://www.food2fork.com/api/search?key=";
    private static final String API_URL_GET_BASE = "https://www.food2fork.com/api/get?key=";
    private static final String API_KEY = "80665d2ce31ddd34451bfaa7653bc773";
    private static final String API_SEARCH_END = "&q=";
    private static final String API_GET_END = "&rId=";

    private static List<String> list = new ArrayList<>();

    private String id;

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

        // load recyclerview for ingredients and recipes
        showRecyclerView(view);

        // check if user has zero recipes. If yes -> load recipes from food2fork
        checkRecipe();

        // this causes the error of recycler view only appearing
        // if click on ingredient form (but don't fill in anything)
        //meatRecyclerView.setHasFixedSize(true);

        return view;
    }

    public void showRecyclerView(View view) {

        // meat
        meatRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_meat");
        meatQuery = meatRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // grains
        grainsRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_grains");
        grainsQuery = grainsRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // veg
        vegRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_vegetable");
        vegQuery = vegRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // dairy
        dairyRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_dairy");
        dairyQuery = dairyRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // sauces
        saucesRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_sauces");
        saucesQuery = saucesRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // condiment
        condRef = db.collection("users").document(ProfileForm.user.getUid()).collection("ingredient_condiment");
        condQuery = condRef.orderBy("numDays", Query.Direction.ASCENDING);
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
                IngredientFormView ingredientFragmentView = new IngredientFormView();

                // pass reference to ingredient fragment
                ingredientFragmentView.passReference(ingredient, id, path);

                goToFragment(ingredientFragmentView);
            }
        });

        // recipe
        recipeRef = db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
        recipeQuery = recipeRef.orderBy("name", Query.Direction.ASCENDING);
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
                id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                ExploreFragment.recentDocumentID = id;

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

    public void checkRecipe() {
        db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            int count = 0;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        count++;
                        if (count >= 1) {
                            break;
                        }
                        Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                    }
                    if (count == 0) {
                        Toast.makeText(getContext(), "Loading recipes...", Toast.LENGTH_LONG).show();
                        showDatabaseRecipe();
                    }
                } else {
                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void showDatabaseRecipe() {
        // Getting recipes according to ingredients
        DownloadTask task = new DownloadTask();
        String website = API_URL_SEARCH_BASE + API_KEY + API_SEARCH_END;
        for (String s : list) {
            website += "," + s;
        }
        try {
            task.execute(website);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Downloading web data
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                JSONObject jsonObject = new JSONObject(result);
                JSONArray recipes = (JSONArray) jsonObject.get("recipes");
                // for every recipe
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject details = (JSONObject) recipes.get(i);

                    //Getting every recipe ingredients
                    String recipeURL = API_URL_GET_BASE + API_KEY + API_GET_END
                            + details.getString("recipe_id");

                    List<String> ingredientList = new ArrayList<>();
                    try {
                        url = new URL(recipeURL);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream input = urlConnection.getInputStream();
                        InputStreamReader readers = new InputStreamReader(input);
                        int output = readers.read();
                        String ingredients = "";
                        while (output != -1) {
                            char current = (char) output;
                            ingredients += current;
                            output = readers.read();
                        }
                        JSONObject ingredientsObject = new JSONObject(ingredients);
                        JSONObject detailsObject = (JSONObject) ingredientsObject.get("recipe");
                        JSONArray ingredientArray = (JSONArray) detailsObject.get("ingredients");

                        for (int k = 0; k < ingredientArray.length(); k++) {
                            ingredientList.add(ingredientArray.get(k).toString());
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    if (id == null) {
                        recipeRef.add(new RecipeItem(details.getString("title"),
                                ingredientList,
                                "", id, details.getString("source_url")));
                    } else {
                        recipeRef.document(id).set(new RecipeItem(details.getString("title"),
                                ingredientList,
                                "", id, details.getString("source_url")), SetOptions.merge());
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
