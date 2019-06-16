package com.example.bakingtime.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.bakingtime.data.Ingredient;
import com.example.bakingtime.data.Recipe;
import com.example.bakingtime.data.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving recipe data.
 */
public final class NetworkJsonUtilities {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NetworkJsonUtilities.class.getSimpleName();
    /**
     * Keys for the json response for Recipe
     */
    private static final String RECIPE_ID = "id";
    private static final String RECIPE_NAME = "name";
    /**
     * Keys for the json response for Ingredient
     */
    private static final String INGREDIENT_ARRAY = "ingredients";
    private static final String INGREDIENT_QUANTITY = "quantity";
    private static final String INGREDIENT_MEASURE = "measure";
    private static final String SINGLE_INGREDIENT = "ingredient";
    /**
     * Keys for the json response for Step
     */
    private static final String STEP_ARRAY = "steps";
    private static final String STEP_ID = "id";
    private static final String STEP_SHORT_DESCRIPTION = "shortDescription";
    private static final String STEP_DESCRIPTION = "description";
    private static final String STEP_VIDEO_URL = "videoURL";
    private static final String STEP_THUMBNAIL_URL = "thumbnailURL";


    private NetworkJsonUtilities() {
    }

    /**
     * return a list of {@link Recipe} objects.
     */
    public static List<Recipe> fetchRecipeData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Recipe}s
        List<Recipe> recipes = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Recipe}s
        return recipes;
    }


    /**
     * return a list of {@link Ingredient} objects.
     */
    public static List<Ingredient> fetchIngredientData(String requestUrl, int position) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and using adapter position to create a list of {@link Ingredient}s
        List<Ingredient> ingredients = extractIngredientFromJson(jsonResponse, position);

        // Return the list of {@link Ingredient}s
        return ingredients;
    }


    /**
     * return a list of {@link Step} objects.
     */
    public static List<Step> fetchStepData(String requestUrl, int position) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and using adapter position to create a list of {@link Step}s
        List<Step> steps = extractStepFromJson(jsonResponse, position);

        // Return the list of {@link Step}s
        return steps;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the recipe JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Recipe} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Recipe> extractFeatureFromJson(String recipeJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding recipes to
        List<Recipe> recipes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONArray from the JSON response string
            // which represents a list of features (or recipes).
            JSONArray baseJsonResponse = new JSONArray(recipeJSON);

            // For each recipe in the baseJsonResponse, create an {@link Recipe} object
            for (int i = 0; i < baseJsonResponse.length(); i++) {

                // Get a single recipe at position i within the list of recipes
                JSONObject currentRecipe = baseJsonResponse.getJSONObject(i);

                // Extract the value for the key called "id"
                int recipeId = currentRecipe.optInt(RECIPE_ID);

                // Extract the value for the key called "name"
                String recipeName = currentRecipe.getString(RECIPE_NAME);

                // Create a new {@link Recipe} object with the id and name from the JSON response.
                Recipe recipe = new Recipe(recipeId, recipeName);

                // Add the new {@link Recipe} to the list of recipes.
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the recipe JSON results", e);
        }

        // Return the list of recipes
        return recipes;
    }


    /**
     * Return a list of {@link Ingredient} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Ingredient> extractIngredientFromJson(String recipeJSON, int position) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding ingredients to
        List<Ingredient> ingredients = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONArray from the JSON response string
            // which represents a list of features (or recipes).
            JSONArray baseJsonResponse = new JSONArray(recipeJSON);

            // Create a JSONObject from the baseJsonResponse array
            // which represents the selected recipe.
            // Here we are using the adapter position to know which recipe is selected
            JSONObject recipe = baseJsonResponse.optJSONObject(position);

            // Create a JSONArray from the recipe JSONObject
            // which represents the recipe ingredients.
            JSONArray ingredientArray = recipe.optJSONArray(INGREDIENT_ARRAY);

            // For each ingredient in the ingredientArray, create an {@link Ingredient} object
            for (int i = 0; i < ingredientArray.length(); i++) {

                // Get a single ingredient at position i within the list of ingredients
                JSONObject currentIngredient = ingredientArray.optJSONObject(i);

                // Extract the value for the key called "quantity"
                int quantity = currentIngredient.optInt(INGREDIENT_QUANTITY);

                // Extract the value for the key called "measure"
                String measure = currentIngredient.optString(INGREDIENT_MEASURE);

                // Extract the value for the key called "ingredient"
                String singleIngredient = currentIngredient.optString(SINGLE_INGREDIENT);

                // Create a new {@link Ingredient} object with the quantity, measure
                // and singleIngredient from the JSON response.
                Ingredient ingredient = new Ingredient(quantity, measure, singleIngredient);

                // Add the new {@link Ingredient} to the list of ingredients.
                ingredients.add(ingredient);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the ingredient JSON results", e);
        }

        // Return the list of ingredients
        return ingredients;
    }


    /**
     * Return a list of {@link Step} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Step> extractStepFromJson(String recipeJSON, int position) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding steps to
        List<Step> steps = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONArray from the JSON response string
            // which represents a list of features (or recipes).
            JSONArray baseJsonResponse = new JSONArray(recipeJSON);

            // Create a JSONObject from the baseJsonResponse array
            // which represents the selected recipe.
            // Here we are using the adapter position to know which recipe is selected.
            JSONObject recipe = baseJsonResponse.optJSONObject(position);

            // Create a JSONArray from the recipe JSONObject
            // which represents the recipe steps.
            JSONArray stepArray = recipe.optJSONArray(STEP_ARRAY);

            // For each step in the stepArray, create an {@link Step} object
            for (int i = 0; i < stepArray.length(); i++) {

                // Get a single step at position i within the list of steps
                JSONObject currentStep = stepArray.optJSONObject(i);

                // Extract the value for the key called "id"
                int stepId = currentStep.optInt(STEP_ID);

                // Extract the value for the key called "shortDescription"
                String shortDescription = currentStep.optString(STEP_SHORT_DESCRIPTION);

                // Extract the value for the key called "description"
                String description = currentStep.optString(STEP_DESCRIPTION);

                // Extract the value for the key called "videoURL"
                String videoURL = currentStep.optString(STEP_VIDEO_URL);
                if (videoURL.isEmpty()) {
                    // If videoURL is empty extract the value for the key called "thumbnailURL"
                    videoURL = currentStep.optString(STEP_THUMBNAIL_URL);
                }

                // Create a new {@link Step} object with the stepId, shortDescription, description
                // and videoURL from the JSON response.
                Step step = new Step(stepId, shortDescription, description, videoURL);

                // Add the new {@link Step} to the list of steps.
                steps.add(step);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the step JSON results", e);
        }

        // Return the list of steps
        return steps;
    }


}

