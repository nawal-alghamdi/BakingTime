package com.example.bakingtime.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.bakingtime.data.Ingredient;
import com.example.bakingtime.data.Step;

import java.util.List;

public class RecipeFragmentAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    public RecipeFragmentAdapter(Context context, FragmentManager fm, List<Ingredient> ingredients, List<Step> steps) {
        super(fm);
        this.context = context;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return IngredientFragment.newInstance(ingredients);
        } else {

            // (position - 1) because the position that was passed is the adapter position
            // And the adapter contain ingredient in position 0 so if I want to get the correct
            // step position, I should subtract position by one to act as the beginning of
            // the list doesn't contain the item ingredient and it just start from step list
            return StepFragment.newInstance(position - 1, steps);
        }
    }

    @Override
    public int getCount() {
        return steps.size() + 1;
    }
}
