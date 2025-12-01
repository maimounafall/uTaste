package com.example.utaste.backend;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

/**
 * Global application class – keeps one single backend instance
 */
public class UTasteApplication extends Application {

    private static UTasteApplication instance;
    private UTasteBackend backend;
    private AppDatabase db;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (backend == null) {
            db = Room.databaseBuilder(
                    this,
                    AppDatabase.class,
                    "utaste.db"
            ).allowMainThreadQueries().build();

            backend = new UTasteBackend(db);
            Log.i("UTasteApp", "Backend initialized once: " + backend);
        } else {
            Log.w("UTasteApp", "Backend already existed, reusing instance");
        }
    }

    public static synchronized UTasteApplication getInstance() {
        return instance;
    }

    public UTasteBackend getBackend() {
        return backend;
    }

    public AppDatabase getDatabase() {
        return db;
    }
}
