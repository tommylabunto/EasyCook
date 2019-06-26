package com.example.easycook.Home.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeItem {

    private String name;
    private List<String> ingredient;
    private String preparation;


    // used to identify the document
    private String documentID;

    // no argument constructor
    public RecipeItem() {
    }

    public RecipeItem(String name, List<String> ingredient, String preparation, String documentID) {
        this.name = name;
        this.ingredient = ingredient;
        this.preparation = preparation;
        this.documentID = documentID;
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

    public String getDocumentID() {
        return documentID;
    }
}
