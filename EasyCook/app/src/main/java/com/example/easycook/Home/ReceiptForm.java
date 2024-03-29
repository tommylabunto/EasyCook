package com.example.easycook.Home;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.easycook.Home.Ingredient.IngredientItem;
import com.example.easycook.R;
import com.example.easycook.Settings.ProfileForm;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ReceiptForm extends Fragment {

    private final String LOG_TAG = "ReceiptForm";

    private ImageView receiptImage;
    private ImageView chooseReceiptButton;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri receiptUri;

    public ReceiptForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receipt_form, container, false);

        // inflate toolbar
        setHasOptionsMenu(true);

        receiptImage = view.findViewById(R.id.receipt_image);
        chooseReceiptButton = view.findViewById(R.id.choose_image_button_receipt);
        chooseReceiptButton.setOnClickListener(new View.OnClickListener() {
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
                if (receiptUri != null) {
                    readReceipt();
                    goToFragment(new HomeFragment());
                } else {
                    showToast(getString(R.string.no_image_found));
                    Log.d(LOG_TAG, "No image found");
                }
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
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
            receiptUri = data.getData();

            Picasso.get().load(receiptUri).into(receiptImage);
        }
    }

    private void readReceipt() {

        // create FirebaseVisionImage
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getContext(), receiptUri);

            // use on-device model
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            // pass image to processImage
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    processText(firebaseVisionText);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processText(FirebaseVisionText firebaseVisionText) {

        int count = 0;

        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0) {
            showToast(getString(R.string.no_text_found));
            Log.d(LOG_TAG, "No text found");
            return;
        }

        for (FirebaseVisionText.TextBlock block : blocks) {
            String blockText = block.getText();

            for (FirebaseVisionText.Line line : block.getLines()) {
                String lineText = line.getText().toLowerCase();

                // sort and add into firebase
                if (sortIngredient(lineText)) {
                    count++;
                }

                // if need to use each word
                /*
                for (FirebaseVisionText.Element element : line.getElements()) {
                    String elementText = element.getText();
                } */
            }
        }

        if (count > 0) {
            showToast(count + " " + getString(R.string.ingredients_added));
        } else {
            showToast(getString(R.string.no_ingredients_added));
        }

    }

    // use hashmap if it gets big
    private boolean sortIngredient(String ingredientName) {

        String ingredientType;
        IngredientItem ingredientItem;

        if (ingredientName.contains("spinach") || ingredientName.contains("radish")) {

            ingredientType = "Vegetable";
            ingredientItem = new IngredientItem(ingredientType, ingredientName, 0, "", 0, "qty", ProfileForm.user.getUid());
            CollectionReference vegRef = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_vegetable");
            vegRef.add(ingredientItem);

            return true;
        }

        if (ingredientName.contains("curry")) {

            ingredientType = "Sauces";
            ingredientItem = new IngredientItem(ingredientType, ingredientName, 0, "", 0, "qty", ProfileForm.user.getUid());
            CollectionReference saucesRef = FirebaseFirestore.getInstance()
                    .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_sauces");
            saucesRef.add(ingredientItem);

            return true;
        }

        return false;
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);

        // add to back stack so user can navigate back
        transaction.addToBackStack(null);

        // make changes
        transaction.commit();
    }

    private void showToast(String msg) {

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
        Log.d(LOG_TAG, "onStart");
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
