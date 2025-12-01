package com.example.utaste.backend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(RecipeIngredientXRef xref);

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId AND ingredientId = :ingredientId")
    void remove(long recipeId, long ingredientId);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredientXRef> listForRecipe(long recipeId);

    @Query("DELETE FROM recipe_ingredients")
    void deleteAll();
}
