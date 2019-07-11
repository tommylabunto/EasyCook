package com.example.easycook.Settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easycook.Home.HomeFragment;
import com.example.easycook.MainActivity;
import com.example.easycook.R;
import com.example.easycook.SignInFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.app.AlertDialog;

// To restart, go AVD manager -> more -> wipe data || file -> invalidate cache & restart
public class SettingsFragment extends Fragment {

    private String LOG_TAG = "SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // click profile
        TextView profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // replace container view (the main activity container) with ingredient fragment
                ProfileForm profileForm = new ProfileForm();
                goToFragment(profileForm);
            }
        });

        // click contact support
        TextView contactSupport = view.findViewById(R.id.contact_support);
        contactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                composeEmail();
            }
        });

        // click signed out
        TextView signOut = view.findViewById(R.id.sign_out_settings);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOutUser();
            }
        });

        // click delete account
        TextView delete = view.findViewById(R.id.delete_account);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // show dialog
                alertUser();
            }
        });

        return view;
    }

    private void composeEmail() {

        Intent intent = new Intent(Intent.ACTION_SENDTO);

        // only email apps should handle this
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.putExtra(Intent.EXTRA_SUBJECT, "EasyCook Support:");

        // Find an activity to hand the intent and start that activity.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void signOutUser() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth != null) {
            mAuth.signOut();
            backToSignIn();
        }
    }

    public void alertUser() {
        AlertDialog.Builder myAlertBuilder =
                new AlertDialog.Builder(getContext());

        // Set the dialog title and message.
        myAlertBuilder.setTitle("Delete Account");
        myAlertBuilder.setMessage("Are you sure you want to delete your account?");

        // if okay
        myAlertBuilder.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUserFirestore();
                        deleteUserAuth();
                    }
                });

        // if cancel
        myAlertBuilder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog.
                    }
                });

        // Create and show the AlertDialog.
        myAlertBuilder.show();
    }

    // delete user on firebase authentication
    private void deleteUserAuth() {

        final Context tempContext = getContext();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // deletes user on firebase auth
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast(tempContext, "User account deleted.");
                            Log.d(LOG_TAG, "User account deleted.");
                            goToFragment(new SignInFragment());
                        }
                    }
                });
    }

    // TODO permission denied (3)
    // delete recipe/ingredient on firestore associated with user
    private void deleteUserFirestore() {

        FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_meat").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String path = document.getReference().getPath();

                                FirebaseFirestore.getInstance().document(path).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(LOG_TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(LOG_TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        } else {
                        }
                    }
                });
    }

    private void showToast(Context tempContext, String msg) {

        Toast.makeText(tempContext, msg, Toast.LENGTH_LONG).show();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();

    }

    private void backToSignIn() {

        // remove all previous fragments stored in back stack
        // so user cannot go back after sign out
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new SignInFragment());

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
