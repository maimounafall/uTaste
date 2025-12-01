package com.example.utaste.backend;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;


@Dao
public interface UserDao {


    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);


    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserEntity findById(long userId);


    @Insert
    long insert(UserEntity u);


    @Update
    void update(UserEntity u);


    @Query("SELECT * FROM users WHERE role = 'WAITER'")
    List<UserEntity> listWaiters();


    @Query("DELETE FROM users WHERE role = 'WAITER'")
    void deleteAllWaiters();

    @Query("SELECT * FROM users WHERE role = :role")
    List<UserEntity> findByRole(Role role);

    @Query("DELETE FROM users WHERE id = :id")
    void deleteById(long id);

    @Query("UPDATE users SET passwordHash = :hash, isDefaultPwd = 1, updatedAt = :ts WHERE id = :userId")
    void resetPassword(long userId, String hash, long ts);
    @Query("SELECT * FROM users")
    List<UserEntity> findAll();
    @Query("DELETE FROM users")
    void deleteAll();

}