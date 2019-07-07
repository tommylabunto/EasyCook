package com.example.easycook.Home.Ingredient;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IngredientItem {

    private String LOG_TAG = "IngredientItem";

    private String ingredientType;
    private String ingredientName;
    private int weight;
    private String expiry;

    // used to sort ingredients
    public int numDays;

    private String units;

    // no argument constructor
    public IngredientItem() {
    }

    public IngredientItem(String ingredientType, String ingredientName, int weight
            , String expiry, int number, String units) {

        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
        this.weight = weight;
        this.expiry = expiry;

        this.numDays = countNumDays(expiry);

        this.units = units;
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

        if (expiry.isEmpty()) {
            return 0;
        }

        try {
            // convert into number of days
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            Date expiryDate = df.parse(expiry);
            Calendar current = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiryDate);

            // not accurate, but can compare
            // positive number means not expired
            // negative number means expired already
            num = (calendar.get(Calendar.DAY_OF_MONTH)
                    + (calendar.get(Calendar.MONTH) * 30)
                    + (calendar.get(Calendar.YEAR) * 12 * 30)) -
                    (current.get(Calendar.DAY_OF_MONTH)
                            + (current.get(Calendar.MONTH) * 30)
                            + (current.get(Calendar.YEAR) * 12 * 30));
        } catch (ParseException e) {
            Log.w(LOG_TAG, e.toString());
        }
        return num;
    }

    public String getUnits() {
        return units;
    }
}
