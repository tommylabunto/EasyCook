package com.example.easycook.Home.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeItem {

    private String name;
    private List<String> ingredient;
    private String preparation;

    // url of food2fork recipe
    private String url;

    // link to download image
    private String imageLink;

    // used to identify the document
    private String documentID;

    // path in firebase storage
    private String path;

    // no argument constructor
    public RecipeItem() {
    }

    public RecipeItem(String name, List<String> ingredient, String preparation, String documentID, String url, String imageLink, String path) {
        this.name = name;
        this.ingredient = ingredient;
        this.preparation = preparation;
        this.documentID = documentID;
        this.url = url;
        this.imageLink = imageLink;
        this.path = path;
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

    public String getUrl() {
        return url;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getPath() {
        return path;
    }
}
