package com.example.utaste.backend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SaleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertSale(Sale sale);
    @Query("DELETE FROM sales WHERE id = :saleId")
    void deleteById(long saleId);
    @Query("SELECT * FROM sales WHERE recipeId = :recipeId ORDER BY saleDateMillis DESC")
    List<Sale> getSalesForRecipe(long recipeId);

    @Query(
            "SELECT r.id AS recipeId, r.name AS recipeName, " +
                    "COUNT(s.id) AS totalSales, COALESCE(AVG(s.rating), 0) AS averageRating " +
                    "FROM recipes r " +
                    "LEFT JOIN sales s ON s.recipeId = r.id " +
                    "GROUP BY r.id, r.name " +
                    "HAVING COUNT(s.id) > 0 " +
                    "ORDER BY totalSales DESC, recipeName ASC"
    )
    List<RecipeSalesStats> getRecipeSalesStats();

    @Query("SELECT COALESCE(AVG(rating), 0) FROM sales WHERE recipeId = :recipeId")
    float getAverageRatingForRecipe(long recipeId);
}
