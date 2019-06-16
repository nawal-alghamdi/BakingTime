package com.example.bakingtime.data;

public class Recipe {

    // Recipe recipeId
    private int recipeId;
    // Recipe name
    private String recipeName;

    public Recipe(int id, String recipeName) {
        this.recipeId = id;
        this.recipeName = recipeName;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }
}
