package com.example.easycook.Home.Ingredient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IngredientItem {

    private String ingredientType;
    private String ingredientName;
    private int weight;
    private String expiry;

    // used to sort ingredients
    private int numDays;

    // no argument constructor
    public IngredientItem() {
    }

    public IngredientItem(String ingredientType, String ingredientName, int weight
            , String expiry, int number) {

        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
        this.weight = weight;
        this.expiry = expiry;

        this.numDays = countNumDays(expiry);
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

    public int getNumDays() {
        return numDays;
    }

    public int countNumDays(String expiry) {

        int num = 0;

        try {
            // convert into number of days
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date expiryDate = df.parse(expiry);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiryDate);

            // not accurate, but can compare
            num = calendar.get(Calendar.DAY_OF_WEEK)
                    + (calendar.get(Calendar.MONTH) * 30)
                    + (calendar.get(Calendar.YEAR) * 12 * 30);
        } catch (ParseException e) {
            System.err.println(e.toString());
        }
        return num;
    }
}
