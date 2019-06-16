package com.example.bakingtime.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.bakingtime.data.Recipe;
import com.example.bakingtime.utilities.NetworkJsonUtilities;

import java.util.List;

/**
 * To define the RecipeLoader class, we extend AsyncTaskLoader and specify List as the generic parameter,
 * which explains what type of data is expected to be loaded.
 * In this case, the loader is loading a list of Recipe objects.
 * <p>
 * Loads a list of recipes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = RecipeLoader.class.getName();

    /* This List will hold and help cache our review data */
    List<Recipe> recipesData = null;

    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link RecipeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public RecipeLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    // We override the onStartLoading() method to call forceLoad() which is a required
    // step to actually trigger the loadInBackground() method to execute.
    @Override
    protected void onStartLoading() {
        if (recipesData != null) {
            deliverResult(recipesData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
     * from NetworkJsonUtilities in the background.
     *
     * @return Recipe data from NetworkJsonUtilities as a List of Recipes.
     * null if an error occurs
     */
    @Override
    public List<Recipe> loadInBackground() {
        try {
            List<Recipe> jsonRecipeResponse = NetworkJsonUtilities.fetchRecipeData(url);
            return jsonRecipeResponse;

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
    public void deliverResult(List<Recipe> data) {
        recipesData = data;
        super.deliverResult(data);
    }
}
