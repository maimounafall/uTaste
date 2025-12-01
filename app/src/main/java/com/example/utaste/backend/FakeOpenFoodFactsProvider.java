package com.example.utaste.backend;

import com.example.utaste.backend.NutritionInfo;

/**
 * Fournisseur nutritionnel factice (fake) pour les tests
 * ou pour les cas où on ne veut pas appeler OpenFoodFacts.
 *
 * Utile uniquement lorsque RecipeService est construit avec :
 * new RecipeService(db)
 */
public class FakeOpenFoodFactsProvider implements NutritionProvider {

    @Override
    public NutritionInfo getNutritionByBarcode(String barcode) {

        // Valeurs nutritionnelles par défaut (0) pour éviter les crashs
        NutritionInfo info = new NutritionInfo();
        info.energyKcalPer100g = 0f;
        info.carbsPer100g = 0f;
        info.proteinsPer100g = 0f;
        info.fatPer100g = 0f;
        info.saltPer100g = 0f;
        info.fibersPer100g = 0f;

        return info;
    }
}
