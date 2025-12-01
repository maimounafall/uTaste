package com.example.utaste.backend;

public class RecipeSalesStats {

    public long recipeId;
    public String recipeName;
    public int totalSales;
    public float averageRating;

    public RecipeSalesStats(long recipeId, String recipeName, int totalSales, float averageRating) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.totalSales = totalSales;
        this.averageRating = averageRating;
    }
}
