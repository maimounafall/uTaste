package com.example.utaste.backend;

import com.example.utaste.backend.NutritionInfo;

/**
 * Interface définissant la récupération des informations nutritionnelles d’un ingrédient.
 */
public interface NutritionProvider {
    NutritionInfo getNutritionByBarcode(String barcode);
}
