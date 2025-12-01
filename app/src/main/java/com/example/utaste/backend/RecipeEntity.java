package com.example.utaste.backend;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
    tableName = "recipes",
    indices = {
    @Index(value = {"name"}, unique = true)
    }
)
    public class RecipeEntity {


    @PrimaryKey(autoGenerate = true)
    public long id;


    @NonNull
    public String name;


    @NonNull
    public String imagePath; // chemin local


    public String description;


    public long createdByUserId;


    public long createdAt;


    public long updatedAt;


    public RecipeEntity() {
    // constructeur vide
    }
    @Ignore
    public RecipeEntity(@NonNull String name, String description, String imagePath, long createdAt) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }
    @Ignore
    public int salesCount = 0;

    @Ignore
    public float averageRating = 0f;

}