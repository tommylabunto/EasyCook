package com.example.easycook.Home.Recipe;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easycook.MainActivity;
import com.example.easycook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

// when clicked on existing recipe
public class RecipeFormView extends Fragment {

    private final String LOG_TAG = "RecipeFormView";

    // referenced passed from home fragment
    private RecipeItem recipe;
    private String id;
    private String path;

    private View view;

    public RecipeFormView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recipe_form_view, container, false);

        this.view = view;

        // inflate toolbar
        setHasOptionsMenu(true);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        // when clicked on url, opens up the page
        final TextView urlEditText = (TextView) view.findViewById(R.id.url_input_view);
        urlEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the URL text.
                String url = urlEditText.getText().toString();
                openURL(url);
            }
        });
        return view;
    }

    // Inflates the menu, and adds items to the action bar if it is present.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_recipe_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handles app bar item clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_edit_button:
                editRecipe();
                return true;
            case R.id.recipe_share_button:
                shareRecipe();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
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

                            TextView nameEditText = (TextView) view.findViewById(R.id.recipe_name_input_view);
                            TextView ingredientEditText = (TextView) view.findViewById(R.id.ingredient_list_input_view);
                            TextView preparationEditText = (TextView) view.findViewById(R.id.preparation_input_view);
                            TextView urlEditText = (TextView) view.findViewById(R.id.url_input_view);
                            ImageView recipeImage = (ImageView) view.findViewById(R.id.food_image_view);

                            nameEditText.setText(recipe.getName());

                            // every ingredient go to new line
                            String editIngredientString = recipe.getIngredientString().replace(", ", ",\n");
                            ingredientEditText.setText(editIngredientString);

                            preparationEditText.setText(recipe.getPreparation());

                            if (!recipe.getUrl().isEmpty()) {
                                // underline text
                                SpannableString content = new SpannableString(recipe.getUrl());
                                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                urlEditText.setText(content);
                            }

                            if (!recipe.getImageLink().isEmpty()) {
                                Picasso.get().load(recipe.getImageLink()).into(recipeImage);
                            }

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

    private void editRecipe() {
        // edits recipe
        RecipeForm recipeForm = new RecipeForm();
        recipeForm.passReference(recipe, id, path);
        goToFragment(recipeForm);
    }

    private void openURL(String url) {
        // Parse the URI and create the intent.
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

    private void goToFragment(Fragment fragment) {
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

    // share recipe name and url to social media
    private void shareRecipe() {

        String recipeName = "";
        String recipeUrl = "";

        if (recipe.getName() != null || !recipe.getName().isEmpty()) {
            recipeName = recipe.getName();
        }

        if (recipe.getUrl() != null || !recipe.getUrl().isEmpty()) {
            recipeUrl = recipe.getUrl();
        }

        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                // from the activity that launched share intent
                .from(getActivity())
                // MIME type
                .setType(mimeType)
                // title
                .setChooserTitle(recipeName)
                // text to be shared
                .setText(recipeUrl)
                // show app chooser and send intent
                .startChooser();

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
