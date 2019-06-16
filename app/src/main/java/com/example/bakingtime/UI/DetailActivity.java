package com.example.bakingtime.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.bakingtime.R;
import com.example.bakingtime.data.Ingredient;
import com.example.bakingtime.data.Step;
import com.example.bakingtime.fragments.RecipeFragmentAdapter;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();


    int selectedListPosition;
    List<Ingredient> ingredients;
    List<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null) {
            selectedListPosition = intent.getIntExtra(ListActivity.SELECTED_LIST_POSITION, 0);
            ingredients = intent.getParcelableArrayListExtra(ListActivity.INGREDIENT_LIST);
            steps = intent.getParcelableArrayListExtra(ListActivity.STEP_LIST);
        }

        setTitle(R.string.recipe_detail);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.viewPager);


        // Create an adapter that knows which fragment should be shown on each page
        RecipeFragmentAdapter adapter = new RecipeFragmentAdapter(this, getSupportFragmentManager(), ingredients, steps);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(selectedListPosition);

    }

}
