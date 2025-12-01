package com.example.utaste.backend;

import java.util.ArrayList;
import java.util.List;

public class SellerService {

    private final RecipeDao recipeDao;
    private final SaleDao saleDao;
    private final RecipeService recipeService;

    public SellerService(RecipeDao recipeDao, SaleDao saleDao, RecipeService recipeService) {
        this.recipeDao = recipeDao;
        this.saleDao = saleDao;
        this.recipeService = recipeService;
    }

    // 1) Voir toutes les recettes + bilan calorique
    public List<RecipeWithNutrition> getAllRecipesWithNutrition() {
        List<RecipeEntity> recipes = recipeDao.listAll();
        List<RecipeWithNutrition> result = new ArrayList<>();

        for (RecipeEntity r : recipes) {
            NutritionInfo n = recipeService.computeRecipeNutrition(r.id);
            result.add(new RecipeWithNutrition(r, n));
        }

        return result;
    }

    // Variante : recettes + bilan calorique + moyenne des notes
    public List<RecipeWithNutritionAndRating> getAllRecipesWithNutritionAndRating() {
        List<RecipeEntity> recipes = recipeDao.listAll();
        List<RecipeWithNutritionAndRating> result = new ArrayList<>();

        for (RecipeEntity r : recipes) {
            NutritionInfo n = recipeService.computeRecipeNutrition(r.id);
            float avgRating = saleDao.getAverageRatingForRecipe(r.id);
            result.add(new RecipeWithNutritionAndRating(r, n, avgRating));
        }

        return result;
    }

    // 2) Enregistrer une vente (note + appréciation) avec validations
    public void recordSale(long recipeId, int quantity, float rating, String comment) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0.");
        }

        if (rating < 0f || rating > 5f) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }

        if (comment != null && comment.length() > 500) {
            throw new IllegalArgumentException("Comment is too long (max 500 characters).");
        }

        RecipeEntity recipe = recipeDao.findById(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found.");
        }

        Sale sale = new Sale(
                recipeId,
                quantity,
                rating,
                comment,
                System.currentTimeMillis()
        );

        saleDao.insertSale(sale);
    }

    // 3) Bilan des ventes
    public List<RecipeSalesStats> getSalesReport() {
        return saleDao.getRecipeSalesStats();
    }

    public float getAverageRatingForRecipe(long recipeId) {
        return saleDao.getAverageRatingForRecipe(recipeId);
    }
}
