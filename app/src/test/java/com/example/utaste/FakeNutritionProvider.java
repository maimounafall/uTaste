package com.example.utaste;

import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.NutritionProvider;

public class FakeNutritionProvider implements NutritionProvider {

    @Override
    public NutritionInfo getNutritionByBarcode(String barcode) {
        NutritionInfo i = new NutritionInfo();
        // fake static values for easy test math
        i.energyKcalPer100g = 100f;
        i.carbsPer100g = 10f;
        i.proteinsPer100g = 5f;
        i.fatPer100g = 2f;
        i.saltPer100g = 1f;
        i.fibersPer100g = 3f;
        return i;
    }
}
