package com.example.utaste.backend;

import com.google.gson.annotations.SerializedName;

// OffNutriments sert uniquement à traduire la réponse JSON de l’API OpenFoodFacts vers ton modèle Java.

public class OffNutriments {   

    @SerializedName("energy-kcal_100g")
    public Float energyKcal100g;

    @SerializedName("carbohydrates_100g")
    public Float carbohydrates100g;

    @SerializedName("proteins_100g")
    public Float proteins100g;

    @SerializedName("fat_100g")
    public Float fat100g;

    @SerializedName("salt_100g")
    public Float salt100g;

    @SerializedName("fiber_100g")
    public Float fiber100g;
}
