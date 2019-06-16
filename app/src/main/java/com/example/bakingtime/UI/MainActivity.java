package com.example.bakingtime.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bakingtime.R;
import com.example.bakingtime.adapters.RecipeAdapter;
import com.example.bakingtime.data.Recipe;
import com.example.bakingtime.loaders.RecipeLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Recipe>> {

    public static final String SELECTED_RECIPE_POSITION = "recipePosition";
    public static final String SELECTED_RECIPE_NAME = "recipeName";
    public static final String REQUEST_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RECIPE_LOADER_ID = 0;
    private static int recyclerViewPosition = -1;
    private static int top = -1;
    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(this, this);
        recyclerView.setAdapter(recipeAdapter);

        LoaderManager.getInstance(this).initLoader(RECIPE_LOADER_ID, null, MainActivity.this);

    }

    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RecipeLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> recipes) {
        loadingIndicator.setVisibility(View.INVISIBLE);
        recipeAdapter.setRecipesData(recipes);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {
        recipeAdapter.clear();
    }

    @Override
    public void onClick(Recipe recipe, int adapterPosition) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra(SELECTED_RECIPE_POSITION, adapterPosition);
        intent.putExtra(SELECTED_RECIPE_NAME, recipe.getRecipeName());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoaderManager.getInstance(this).restartLoader(RECIPE_LOADER_ID, null, MainActivity.this);
    }


    @Override
    public void onPause() {
        super.onPause();
        recyclerViewPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        View view = recyclerView.getChildAt(0);
        if (view == null) {
            top = 0;
        } else {
            top = view.getTop() - recyclerView.getPaddingTop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerViewPosition != -1) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(recyclerViewPosition, top);
        }
    }
}
