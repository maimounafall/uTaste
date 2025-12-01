package com.example.utaste;

import android.app.Application;

import androidx.room.Room;

import com.example.utaste.backend.AppDatabase;

import org.robolectric.RuntimeEnvironment;

public class InMemoryDbFactory {

    public static AppDatabase create() {
        Application context = RuntimeEnvironment.getApplication();
        return Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }
}
