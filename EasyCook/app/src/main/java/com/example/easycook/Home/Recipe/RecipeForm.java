package com.example.easycook.Home.Recipe;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.MainActivity;
import com.example.easycook.R;
import com.example.easycook.Settings.ProfileForm;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class RecipeForm extends Fragment {

    private final String LOG_TAG = "RecipeForm";

    private ImageView recipeImage;
    private Button chooseImageButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri foodUri;

    private StorageReference mStorageRef;
    private StorageReference mImageRef;
    private UploadTask uploadTask;
    private String imageLink;

    // referenced passed from recipe form view fragment
    private RecipeItem recipe;
    private String id;
    private String path;

    private View view;

    public RecipeForm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_recipe_form, container, false);

        this.view = view;

        // inflate toolbar
        setHasOptionsMenu(true);

        // if snapshot exist, populate screen with data
        checkIfSnapshotExist(view);

        recipeImage = view.findViewById(R.id.food_image);
        chooseImageButton = view.findViewById(R.id.choose_image_button);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        return view;
    }

    // Inflates the menu, and adds items to the action bar if it is present.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_tick, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Handles app bar item clicks.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tick_button:
                addRecipe();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void addRecipe() {

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
            // upload pic into firebase storage
            // must have pic to add recipe into firestore
            uploadFile(name, Arrays.asList(ingredient.split(" ")), preparation);
        }
        backToHome(getFragmentManager());
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
                            ImageView recipeImage = (ImageView) view.findViewById(R.id.food_image);

                            nameEditText.setText(recipe.getName());
                            ingredientEditText.setText(recipe.getIngredientString());
                            preparationEditText.setText(recipe.getPreparation());

                            if (!recipe.getImageLink().isEmpty()) {
                                Picasso.get().load(recipe.getImageLink()).into(recipeImage);
                            }

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

    // open up choose image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            foodUri = data.getData();

            Picasso.get().load(foodUri).into(recipeImage);
        }
    }

    // get file type (jpeg/png...)
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // upload file to firebase storage
    public void uploadFile(final String name, final List<String> ingredient, final String preparation) {
        if (foodUri != null) {
            // create a storage reference to app
            mStorageRef = FirebaseStorage.getInstance().getReference();

            final String path = ProfileForm.user.getUid() + "." + name
                    + "." + getFileExtension(foodUri);

            // create a reference to image
            // user can only save one image to one distinct recipe name, otherwise will override
            mImageRef = mStorageRef.child(path);

            uploadTask = mImageRef.putFile(foodUri);

            // Register observers to listen for when the download is done or if it fails
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return mImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadLink = downloadUri.toString();
                        addRecipeToFirebase(name, ingredient, preparation, downloadLink, path);
                    } else {
                        // Handle failures
                    }
                }
            });
        }
    }

    public void addRecipeToFirebase(String name, List<String> ingredient, String preparation, String imageLink, String path) {

        // when recipe is first created, id is null
        // but when recipe is clicked / explore page is clicked -> the id is set

        if (id == null) {
            CollectionReference myRecipe = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
            myRecipe.add(new RecipeItem(name, ingredient, preparation, id, "", imageLink, path));
        } else {
            CollectionReference myRecipe = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("my_recipe");
            myRecipe.document(id).set(new RecipeItem(name, ingredient, preparation, id, "", imageLink, path), SetOptions.merge());
        }

        Log.d(LOG_TAG, "saved changes");
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
