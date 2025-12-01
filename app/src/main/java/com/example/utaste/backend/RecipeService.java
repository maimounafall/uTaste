package com.example.utaste.backend;

import java.util.List;

/**
 * Gestion complète des recettes et de leurs ingrédients.
 * Inclut : création, modification, suppression, gestion d’ingrédients,
 * mise à jour du stock et calculs nutritionnels.
 */
public class RecipeService {

    private final RecipeDao recipes;
    private final IngredientDao ingredients;
    private final RecipeIngredientDao xrefs;
    private final NutritionProvider nutritionProvider;

    public RecipeService(AppDatabase db, NutritionProvider nutritionProvider) {
        this.recipes = db.recipeDao();
        this.ingredients = db.ingredientDao();
        this.xrefs = db.recipeIngredientDao();
        this.nutritionProvider = nutritionProvider;
    }

    public RecipeService(AppDatabase db) {
        this(db, new NutritionRepository(db.nutritionDao()));
    }

    // ---------- CRUD recettes ----------

    public long createRecipe(String name, String imagePath, String description, long createdByUserId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        RecipeEntity existing = recipes.findByName(name.trim());
        if (existing != null) {
            throw new IllegalStateException("Recipe name already exists.");
        }

        RecipeEntity r = new RecipeEntity();
        r.name = name.trim();
        r.imagePath = imagePath != null ? imagePath : "";
        r.description = description;
        r.createdByUserId = createdByUserId;
        long now = System.currentTimeMillis();
        r.createdAt = now;
        r.updatedAt = now;
        return recipes.insert(r);
    }

    public void updateRecipe(long recipeId, String name, String imagePath, String description) {
        RecipeEntity r = recipes.findById(recipeId);
        if (r == null) {
            throw new IllegalArgumentException("Recipe not found.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        RecipeEntity same = recipes.findByName(name.trim());
        if (same != null && same.id != recipeId) {
            throw new IllegalStateException("Another recipe already has this name.");
        }

        r.name = name.trim();
        r.imagePath = imagePath != null ? imagePath : "";
        r.description = description;
        r.updatedAt = System.currentTimeMillis();
        recipes.update(r);
    }

    public void deleteRecipe(long recipeId) {
        RecipeEntity r = recipes.findById(recipeId);
        if (r != null) {
            recipes.delete(r);
        }
    }

    public List<RecipeEntity> getAllRecipes() {
        return recipes.listAll();
    }

    // ---------- Lien recette <-> ingrédient ----------

    public void addIngredientFromBarcode(long recipeId, String barcode, float percent, float recipeQuantity) {
        if (percent <= 0 || percent > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100.");
        }

        Ingredient ing = ingredients.findByBarcode(barcode);
        if (ing == null) {
            throw new IllegalArgumentException("Ingredient not found in stock for barcode: " + barcode);
        }

        float usedQuantity = (percent / 100f) * recipeQuantity;

        if (ing.quantity < usedQuantity) {
            throw new IllegalStateException("Not enough stock for ingredient: " + ing.name);
        }

        ing.quantity -= usedQuantity;
        ingredients.update(ing);

        RecipeIngredientXRef x = new RecipeIngredientXRef();
        x.recipeId = recipeId;
        x.ingredientId = ing.id;
        x.quantityPercent = percent;
        xrefs.upsert(x);
    }
    public void addIngredientToRecipe(long recipeId, long ingredientId, float percent) {
        if (percent <= 0 || percent > 100) {
            throw new IllegalArgumentException("Percent must be between 1 and 100.");
        }

        RecipeIngredientXRef x = new RecipeIngredientXRef();
        x.recipeId = recipeId;
        x.ingredientId = ingredientId;
        x.quantityPercent = percent;

        xrefs.upsert(x); // THIS SAVES TO recipe_ingredients TABLE
    }


    public NutritionInfo getIngredientNutrition(String barcode) {
        return nutritionProvider.getNutritionByBarcode(barcode);
    }

    public List<RecipeIngredientXRef> getIngredientsForRecipe(long recipeId) {
        return xrefs.listForRecipe(recipeId);
    }

    public void changeIngredientPercent(long recipeId, long ingredientId, float newPercent) {
        if (newPercent <= 0 || newPercent > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100.");
        }

        RecipeIngredientXRef x = new RecipeIngredientXRef();
        x.recipeId = recipeId;
        x.ingredientId = ingredientId;
        x.quantityPercent = newPercent;
        xrefs.upsert(x);
    }

    public void removeIngredient(long recipeId, long ingredientId) {
        xrefs.remove(recipeId, ingredientId);
    }

    public String getIngredientNameById(long ingredientId) {
        Ingredient ing = ingredients.findById(ingredientId);
        return (ing != null) ? ing.name : null;
    }

    // ---------- Nutrition (Livrable 3) ----------

    public NutritionInfo computeRecipeNutrition(long recipeId) {
        List<RecipeIngredientXRef> list = xrefs.listForRecipe(recipeId);

        if (list.isEmpty()) {
            return null;
        }

        NutritionInfo total = new NutritionInfo();
        total.energyKcalPer100g = 0f;
        total.carbsPer100g = 0f;
        total.proteinsPer100g = 0f;
        total.fatPer100g = 0f;
        total.saltPer100g = 0f;
        total.fibersPer100g = 0f;

        for (RecipeIngredientXRef ref : list) {

            Ingredient ing = ingredients.findById(ref.ingredientId);
            if (ing == null || ing.barcode == null) {
                continue;
            }

            NutritionInfo info = nutritionProvider.getNutritionByBarcode(ing.barcode);
            if (info == null) {
                continue;
            }

            float ratio = ref.quantityPercent / 100f;

            total.energyKcalPer100g += safe(info.energyKcalPer100g) * ratio;
            total.carbsPer100g += safe(info.carbsPer100g) * ratio;
            total.proteinsPer100g += safe(info.proteinsPer100g) * ratio;
            total.fatPer100g += safe(info.fatPer100g) * ratio;
            total.saltPer100g += safe(info.saltPer100g) * ratio;
            total.fibersPer100g += safe(info.fibersPer100g) * ratio;
        }

        return total;
    }

    private float safe(Float v) {
        return v == null ? 0f : v;
    }

    public Ingredient getIngredientById(long ingredientId) {
        return ingredients.findById(ingredientId);
    }
}
