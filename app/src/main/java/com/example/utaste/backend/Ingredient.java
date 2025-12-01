package com.example.utaste.backend;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Ingrédient présent dans le stock du chef.
 */
@Entity(tableName = "ingredients")
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    // code-barres / QR code scanné
    public String barcode;

    // quantité en stock (optionnel selon ton livrable 2)
    public float quantity; // ex: en g ou en unité

    public String unit; // "g", "ml", "pcs"...
}
