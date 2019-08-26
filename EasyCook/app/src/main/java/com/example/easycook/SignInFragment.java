package com.example.easycook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.Home.Ingredient.GenerateTestIngredient;
import com.example.easycook.Home.Recipe.GenerateTestRecipe;
import com.example.easycook.Settings.ProfileForm;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInFragment extends Fragment {

    private String LOG_TAG = "SignInActivity";

    private Snackbar snackbar;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    // random number
    private int RC_SIGN_IN = 1800;

    private SignInButton signInButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sign_in_activity, container, false);

        snackbar = Snackbar.make(view.findViewById(R.id.sign_in_coordinatorLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT);

        // Set the dimensions of the sign-in button.
        signInButton = view.findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // when clicked, sign in to google
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            //showToast(getContext(), getString(R.string.hello_amigo) + " " + user.getDisplayName());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            snackbar.show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    // replace container
    private void updateUI(FirebaseUser user) {

        if (user != null) {

            MainActivity.passAuth(mAuth, user);

            // doesnt work if user sign in, delete all ingredient/recipe, then sign in again
            // check if user has zero recipes. If yes -> load recipes from food2fork
            checkRecipe();

            // check if user has zero ingredients. If yes -> load test recipes
            checkIngredient();

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();

        } else {
            Log.d(LOG_TAG, "no user");
        }
    }

    private void checkRecipe() {

        db.collection("users").document(ProfileForm.user.getUid()).collection("my_recipe")
                .whereEqualTo("author", ProfileForm.user.getUid())
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
                        //showToast(getContext(), getString(R.string.loading_recipes));
                        GenerateTestRecipe.showDatabaseRecipe();
                    }
                } else {
                    Log.w(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void checkIngredient() {

        db.collection("users").document(ProfileForm.user.getUid()).collection("all_ingredients")
                .whereEqualTo("author", ProfileForm.user.getUid())
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
                    }
                    if (count == 0) {
                        //showToast(getContext(), getString(R.string.loading_ingredients));
                        GenerateTestIngredient.generateIngredients();
                    }
                } else {
                    Log.w(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void showToast(Context tempContext, String msg) {

        Toast.makeText(tempContext, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        updateUI(user);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }
}
