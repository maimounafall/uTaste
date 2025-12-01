package com.example.utaste.backend;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IngredientDao {

    @Insert
    long insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Query("SELECT * FROM ingredients WHERE id = :id LIMIT 1")
    Ingredient findById(long id);

    @Query("SELECT * FROM ingredients WHERE barcode = :barcode LIMIT 1")
    Ingredient findByBarcode(String barcode);

    @Query("SELECT * FROM ingredients WHERE name = :name LIMIT 1")
    Ingredient findByName(String name);

    @Query("SELECT * FROM ingredients")
    List<Ingredient> findAll();

    // utile si tu veux décrémenter le stock quand tu utilises un ingrédient
    @Query("UPDATE ingredients SET quantity = quantity - :used WHERE id = :id")
    void decreaseQuantity(long id, float used);

    // -------------- FIX HERE ---------------
    @Query("DELETE FROM ingredients")
    void deleteAll();

    @Query("DELETE FROM ingredients WHERE id = :id")
    void deleteById(long id);
}
