package com.example.utaste.backend;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
    tableName = "users",
    indices = {
    @Index(value = {"email"}, unique = true)
    }
)
public class UserEntity {


    @PrimaryKey(autoGenerate = true)
    public long id;


    @NonNull
    public Role role; // ADMINISTRATOR / CHEF / WAITER


    @NonNull
    public String email;


    public String firstName;


    public String lastName;


    @NonNull
    public String passwordHash;


    public long createdAt;


    public long updatedAt;


    public boolean isDefaultPwd;


    public UserEntity() {
    // constructeur vide
    }
}