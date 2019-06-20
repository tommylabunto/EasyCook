package com.example.easycook.Home.Recipe;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class RecipeItem {

    private String ingredient;
    private String preparation;

    public RecipeItem(String ingredient, String preparation) {
        this.ingredient = ingredient;
        this.preparation = preparation;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getPreparation() {
        return preparation;
    }
}
