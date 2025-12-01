package com.example.utaste.backend;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nutrition_cache")
public class NutritionEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String barcode;

    public Float energyKcalPer100g;
    public Float carbsPer100g;
    public Float proteinsPer100g;
    public Float fatPer100g;
    public Float saltPer100g;
    public Float fibersPer100g;

    public long fetchedAt;
}
