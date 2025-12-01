package com.example.utaste.backend;

public class RecipeWithNutritionAndRating {

    public RecipeEntity recipe;
    public NutritionInfo nutrition;
    public float averageRating;

    public RecipeWithNutritionAndRating(RecipeEntity recipe, NutritionInfo nutrition, float averageRating) {
        this.recipe = recipe;
        this.nutrition = nutrition;
        this.averageRating = averageRating;
    }
}
