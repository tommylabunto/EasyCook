package com.example.easycook.Home.Recipe;

public class RecipeItem {

    private String name;
    private String ingredient;
    private String preparation;

    // no argument constructor
    public RecipeItem() {
    }

    public RecipeItem(String name, String ingredient, String preparation) {
        this.name = name;
        this.ingredient = ingredient;
        this.preparation = preparation;
    }

    public String getName() {
        return name;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getPreparation() {
        return preparation;
    }
}
