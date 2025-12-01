package com.example.utaste.backend;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sales",
        foreignKeys = @ForeignKey(
                entity = RecipeEntity.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("recipeId")}
)
public class Sale {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long recipeId;
    public int quantity;
    public float rating;
    public String comment;
    public long saleDateMillis;

    public Sale(long recipeId, int quantity, float rating, String comment, long saleDateMillis) {
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.rating = rating;
        this.comment = comment;
        this.saleDateMillis = saleDateMillis;
    }
}
