package com.example.easycook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class IngredientItem {

    private String ingredientType;
    private String ingredientName;
    private int weight;
    private String expiry;

    public IngredientItem(String ingredientType, String ingredientName, int weight
    , String expiry){

        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
        this.weight = weight;
        this.expiry = expiry;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public int getWeight() {
        return weight;
    }

    public String getExpiry() {
        return expiry;
    }
}
