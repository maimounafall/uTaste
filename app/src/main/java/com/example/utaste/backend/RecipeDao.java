package com.example.utaste.backend;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.utaste.backend.RecipeIngredientXRef;
import java.util.List;


@Dao
public interface RecipeDao {


    @Insert
    long insert(RecipeEntity r);


    @Update
    void update(RecipeEntity r);


    @Delete
    void delete(RecipeEntity r);


    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    RecipeEntity findById(long id);


    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<RecipeEntity> listAll();

    @Query("SELECT * FROM recipes WHERE name = :name LIMIT 1")
    RecipeEntity findByName(String name);

    @Query("SELECT COUNT(*) FROM recipes WHERE name = :name AND id <> :excludeId")
    int countNameExists(String name, long excludeId);


    @Query("DELETE FROM recipes")
    void deleteAll();
}