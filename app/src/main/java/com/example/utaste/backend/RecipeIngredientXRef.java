package com.example.utaste.backend;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "recipe_ingredients",
        primaryKeys = {"recipeId", "ingredientId"},
        foreignKeys = {
                @ForeignKey(entity = RecipeEntity.class,
                        parentColumns = {"id"},
                        childColumns = {"recipeId"},
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ingredient.class,
                        parentColumns = {"id"},
                        childColumns = {"ingredientId"},
                        onDelete = ForeignKey.CASCADE)
        }
)
public class RecipeIngredientXRef {

    public long recipeId;
    public long ingredientId;
    public float quantityPercent;
}
