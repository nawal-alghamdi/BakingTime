package com.example.bakingtime.UI;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.bakingtime.IdlingResource.SimpleIdlingResource;
import com.example.bakingtime.R;
import com.example.bakingtime.data.Ingredient;
import com.example.bakingtime.data.RecipeEvent;
import com.example.bakingtime.data.Step;
import com.example.bakingtime.fragments.ListFragment;
import com.example.bakingtime.fragments.RecipeFragmentAdapter;
import com.example.bakingtime.loaders.IngredientLoader;
import com.example.bakingtime.loaders.StepLoader;
import com.example.bakingtime.widgets.RecipeWidgetProvider;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener,
        LoaderManager.LoaderCallbacks {


    public static final String SELECTED_LIST_POSITION = "selectedListPosition";
    public static final String STEP_LIST = "stepList";
    public static final String INGREDIENT_LIST = "ingredientList";
    private static final String TAG = ListActivity.class.getSimpleName();
    private static final int INGREDIENT_LOADER_ID = 1;
    private static final int STEP_LOADER_ID = 2;
    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean twoPane;
    private int selectedRecipePosition;
    private String selectedRecipeName;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Inside onCreate");
        setContentView(R.layout.activity_list);

        ingredients = new ArrayList<>();
        steps = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            selectedRecipePosition = intent.getIntExtra(MainActivity.SELECTED_RECIPE_POSITION, 0);
            selectedRecipeName = intent.getStringExtra(MainActivity.SELECTED_RECIPE_NAME);
            Log.d(TAG, "Inside Intent recipeName " + selectedRecipeName);
        }

        setTitle(selectedRecipeName);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager.getInstance(this).initLoader(INGREDIENT_LOADER_ID, null, this);
        LoaderManager.getInstance(this).initLoader(STEP_LOADER_ID, null, this);

        // Determine if you're creating a two-pane or single-pane display
        // This viewPager will only initially exist in the two-pane tablet case
// We're in single-pane mode and displaying fragments on a phone in separate activities
        twoPane = findViewById(R.id.viewPager) != null;

        // Get the IdlingResource instance
        getIdlingResource();

    }

    @Override
    public void onFragmentInteraction(int position) {
        Log.d(TAG, "Inside onFragmentInteraction");
        if (twoPane) {
            // Find the view pager that will allow the user to swipe between fragments
            ViewPager viewPager = findViewById(R.id.viewPager);

            // Create an adapter that knows which fragment should be shown on each page
            RecipeFragmentAdapter adapter = new RecipeFragmentAdapter(this, getSupportFragmentManager(), ingredients, steps);

            // Set the adapter onto the view pager
            viewPager.setAdapter(adapter);

            //To open specific page when selected from the list
            viewPager.setCurrentItem(position);

        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(SELECTED_LIST_POSITION, position);
            intent.putExtra(MainActivity.SELECTED_RECIPE_NAME, selectedRecipeName);
            intent.putParcelableArrayListExtra(STEP_LIST, (ArrayList<? extends Parcelable>) steps);
            intent.putParcelableArrayListExtra(INGREDIENT_LIST, (ArrayList<? extends Parcelable>) ingredients);
            startActivity(intent);
        }

    }


    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        Log.d(TAG, "Inside onCreateLoader");

        String requestUrl = MainActivity.REQUEST_URL;

        if (id == INGREDIENT_LOADER_ID) {
            return new IngredientLoader(this, requestUrl, selectedRecipePosition);

        } else if (id == STEP_LOADER_ID) {
            return new StepLoader(this, requestUrl, selectedRecipePosition);
        }
        return null;
    }

    // Called when a previously created loader has finished its load.
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        // Id to find which loader is called
        int id = loader.getId();

        if (id == INGREDIENT_LOADER_ID) {
            ingredients = (List<Ingredient>) data;
            saveIngredientInSharedPreference(ingredients);

        } else if (id == STEP_LOADER_ID) {
            steps = (List<Step>) data;
        }

        EventBus.getDefault().post(new RecipeEvent(selectedRecipePosition, selectedRecipeName, ingredients, steps));

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void saveIngredientInSharedPreference(List<Ingredient> ingredients) {
        final SharedPreferences sharedPreferences = getSharedPreferences(RecipeWidgetProvider.RECIPE_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(ingredients);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RecipeWidgetProvider.SP_INGREDIENT_JSON, json);
        editor.putString(RecipeWidgetProvider.SP_RECIPE_NAME_STRING, selectedRecipeName);
        editor.apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_widget);

        // Update all widgets, and this is necessary to update the recipeName because
        // the code for updating widget recipe name is inside updateAppWidget method
        for (int appWidgetId : appWidgetIds) {
            RecipeWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId);
        }
    }

}
