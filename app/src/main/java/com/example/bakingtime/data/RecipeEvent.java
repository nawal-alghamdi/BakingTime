package com.example.bakingtime.data;

import java.util.List;

public class RecipeEvent {

    // Adapter position to know which recipe is selected
    private int position;

    // To store and get the selected recipe name
    private String recipeName;

    // The selected recipe ingredients
    private List<Ingredient> ingredients;

    // The selected recipe steps
    private List<Step> steps;


    public RecipeEvent(int position, String recipeName, List<Ingredient> ingredients, List<Step> steps) {
        this.position = position;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int eventGetId() {
        return position;
    }

    public String eventGetRecipeName() {
        return recipeName;
    }

    public List<Ingredient> eventGetIngredients() {
        return ingredients;
    }

    public List<Step> eventGetSteps() {
        return steps;
    }
}
