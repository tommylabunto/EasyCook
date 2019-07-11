package com.example.easycook.Home.Recipe;

import android.os.AsyncTask;

import com.example.easycook.Settings.ProfileForm;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GenerateTestRecipe {

    // keys for API_KEY
    private static final String key1 = "80c4812dfb139f9175e20d9e9a951c17";
    private static final String key2 = "9e7281bd1a13bd325a8bb8f6b3437b68";
    private static final String key3 = "d5359e0e90ce8db59c252521c4f9cf8c";
    private static final String key4 = "5b9abf96fcd9a7e699a7847f36c4fbdc";
    private static final String key5 = "e765ff5f65725fb2dd35d9102de66226";
    private static final String key6 = "ac96dfb1b6e562dcf67e36396bcdfde7";
    private static final String key7 = "1f397beda6e074864648ce46c6a4b4ce";
    private static final String key8 = "72a9e3d8cc17fbce8b47084a00181de9";
    private static final String key9 = "7a6b9d662b13ac4c1f71e3eff78b674d";
    private static final String key10 = "bcee4295121a53d9ca5f481adf6b451d";

    // to display database recipes
    private static final String API_URL_SEARCH_BASE = "https://www.food2fork.com/api/search?key=";
    private static final String API_URL_GET_BASE = "https://www.food2fork.com/api/get?key=";
    // change here
    private static final String API_KEY = key4;
    private static final String API_SEARCH_END = "&q=";
    private static final String API_GET_END = "&rId=";

    private static List<String> list = new ArrayList<>();

    private static CollectionReference recipeRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(ProfileForm.user.getUid())
            .collection("my_recipe");

    public static void showDatabaseRecipe() {

        // Getting recipes according to ingredients
        DownloadTask task = new DownloadTask();
        String website = API_URL_SEARCH_BASE + API_KEY + API_SEARCH_END;
        for (String s : list) {
            website += "," + s;
        }
        try {
            task.execute(website);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Downloading web data
    public static class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                JSONObject jsonObject = new JSONObject(result);
                JSONArray recipes = (JSONArray) jsonObject.get("recipes");
                // for every recipe
                for (int i = 0; i < recipes.length(); i++) {
                    JSONObject details = (JSONObject) recipes.get(i);

                    //Getting every recipe ingredients
                    String recipeURL = API_URL_GET_BASE + API_KEY + API_GET_END
                            + details.getString("recipe_id");

                    List<String> ingredientList = new ArrayList<>();

                    String imageLink = "";
                    try {
                        url = new URL(recipeURL);
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        InputStream input = urlConnection.getInputStream();
                        InputStreamReader readers = new InputStreamReader(input);
                        int output = readers.read();
                        String ingredients = "";
                        while (output != -1) {
                            char current = (char) output;
                            ingredients += current;
                            output = readers.read();
                        }
                        JSONObject ingredientsObject = new JSONObject(ingredients);
                        JSONObject detailsObject = (JSONObject) ingredientsObject.get("recipe");
                        JSONArray ingredientArray = (JSONArray) detailsObject.get("ingredients");

                        for (int k = 0; k < ingredientArray.length(); k++) {
                            ingredientList.add(ingredientArray.get(k).toString());
                        }
                        // dont download and save into firebase storage
                        // save image url in recipeitem
                        // picasso can only load https web image
                        imageLink = details.getString("image_url").replace("http", "https");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    String name = details.getString("title");
                    String source_url = details.getString("source_url");

                    recipeRef.add(new RecipeItem(name, ingredientList,
                            "", "", source_url, imageLink, "", ProfileForm.user.getUid()));

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
