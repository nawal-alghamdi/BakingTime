package com.example.bakingtime.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.bakingtime.data.Ingredient;
import com.example.bakingtime.utilities.NetworkJsonUtilities;

import java.util.List;

/**
 * To define the StepLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
 * which explains what type of data is expected to be loaded.
 * In this case, the loader is loading a list of Ingredient objects.
 * <p>
 * Loads a list of ingredients by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class IngredientLoader extends AsyncTaskLoader<List<Ingredient>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = StepLoader.class.getName();

    /* This List will hold and help cache our ingredient data */
    private List<Ingredient> ingredientsData = null;

    /**
     * Query URL
     */
    private String url;

    /**
     * Position which is the adapter position to find out which recipe the user select and then download
     * the ingredients for the selected recipe
     */
    private int position;

    /**
     * Constructs a new {@link StepLoader}.
     *
     * @param context  of the activity
     * @param url      to load data from
     * @param position of the adapter
     */
    public IngredientLoader(Context context, String url, int position) {
        super(context);
        this.url = url;
        this.position = position;
    }

    // We override the onStartLoading() method to call forceLoad() which is a required
    // step to actually trigger the loadInBackground() method to execute.
    @Override
    protected void onStartLoading() {
        if (ingredientsData != null) {
            deliverResult(ingredientsData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from NetworkJsonUtilities in the background.
     *
     * @return Ingredient data from NetworkJsonUtilities as a List of Ingredients.
     * null if an error occurs
     */
    @Override
    public List<Ingredient> loadInBackground() {
        try {
            List<Ingredient> jsonIngredientResponse = NetworkJsonUtilities.fetchIngredientData(url, position);
            return jsonIngredientResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends the result of the load to the registered listener.
     *
     * @param data The result of the load
     */
    @Override
    public void deliverResult(List<Ingredient> data) {
        ingredientsData = data;
        super.deliverResult(data);
    }
}

