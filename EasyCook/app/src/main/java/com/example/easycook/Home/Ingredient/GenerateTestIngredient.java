package com.example.easycook.Home.Ingredient;

import com.example.easycook.Explore.ExploreFragment;
import com.example.easycook.Settings.ProfileForm;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GenerateTestIngredient {

    public static void generateIngredients() {

        CollectionReference meatRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_meat");

        CollectionReference grainsRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_grains");

        CollectionReference vegRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_vegetable");

        CollectionReference dairyRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_dairy");

        CollectionReference saucesRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_sauces");

        CollectionReference condRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("ingredient_condiment");

        CollectionReference ingredientRef = FirebaseFirestore.getInstance()
                .collection("users").document(ProfileForm.user.getUid()).collection("all_ingredients");

        IngredientItem meat = new IngredientItem("Meat", "1 pound Hot Breakfast Sausage", 450, "18/9/2019", 0, "g");
        IngredientItem grains = new IngredientItem("Grains", "10 ounces dry elbow macaroni", 280, "13/9/2019", 0, "g");
        IngredientItem veg = new IngredientItem("Vegetable", "1 whole Large Onion", 1, "21/9/2019", 0, "qty");
        IngredientItem dairy = new IngredientItem("Dairy", "1 tablespoon fresh lemon juice", 1, "10/9/2019", 0, "tbsp");
        IngredientItem sauces = new IngredientItem("Sauces", "3 tbsp olive oil", 3, "21/9/2019", 0, "tbsp");
        IngredientItem condiment = new IngredientItem("Condiment", "1 tsp pepper", 1, "29/9/2019", 0, "tsp");

        meatRef.add(meat);
        grainsRef.add(grains);
        vegRef.add(veg);
        dairyRef.add(dairy);
        saucesRef.add(sauces);
        condRef.add(condiment);

        ingredientRef.add(meat);
        ingredientRef.add(grains);
        ingredientRef.add(veg);
        ingredientRef.add(dairy);
        ingredientRef.add(sauces);
        ingredientRef.add(condiment);

        // for ingredient closest to expiring
        ExploreFragment.passIngredient(dairy);
    }
}
