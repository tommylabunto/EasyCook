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

public class GenerateTestRecipe {

    // to display database recipes
    private static final String API_URL_SEARCH_BASE = "https://www.food2fork.com/api/search?key=";
    private static final String API_URL_GET_BASE = "https://www.food2fork.com/api/get?key=";
    private static final String API_KEY = "e749d499d08d9eae5a3f5c6fc74a95aa";
    private static final String API_SEARCH_END = "&q=";
    private static final String API_GET_END = "&rId=";

    private static List<String> list = new ArrayList<>();

    private static String id;

    private static CollectionReference recipeRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(ProfileForm.user.getUid())
            .collection("my_recipe");

    public static void showDatabaseRecipe(String id) {

        id = id;

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
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
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
                        urlConnection = (HttpURLConnection) url.openConnection();
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

                    if (id == null) {
                        recipeRef.add(new RecipeItem(name, ingredientList,
                                "", id, source_url, imageLink, "", ProfileForm.user.getUid()));
                    } else {
                        recipeRef.document(id).set(new RecipeItem(name,
                                ingredientList,
                                "", id, source_url, imageLink, "", ProfileForm.user.getUid()), SetOptions.merge());
                    }
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
