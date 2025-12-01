package com.example.utaste.backend;

public class RecipeWithNutrition {

    public RecipeEntity recipe;
    public NutritionInfo nutrition;

    public RecipeWithNutrition(RecipeEntity recipe, NutritionInfo nutrition) {
        this.recipe = recipe;
        this.nutrition = nutrition;
    }
}
