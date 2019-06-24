package com.example.easycook.Home.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeItem {

    private String name;
    private List<String> ingredient;
    private String preparation;

    // no argument constructor
    public RecipeItem() {
    }

    public RecipeItem(String name, List<String> ingredient, String preparation) {
        this.name = name;
        this.ingredient = ingredient;
        this.preparation = preparation;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredient() {
        return ingredient;
    }

    // convert list into one string with spaces in between
    public String getIngredientString() {
        return ingredient.stream().collect(Collectors.joining(" "));
    }

    public String getPreparation() {
        return preparation;
    }
}
