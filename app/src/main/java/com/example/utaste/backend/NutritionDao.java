package com.example.utaste.backend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.utaste.backend.NutritionEntity;


@Dao
public interface NutritionDao {

    @Query("SELECT * FROM nutrition_cache WHERE barcode = :barcode LIMIT 1")
    NutritionEntity findByBarcode(String barcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NutritionEntity entity);
}
