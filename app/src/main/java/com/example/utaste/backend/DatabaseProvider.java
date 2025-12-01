package com.example.utaste.backend;



import android.content.Context;
import androidx.room.Room;

public class DatabaseProvider {

    public static AppDatabase get(Context context) {
        return UTasteApplication.getInstance().getDatabase();
    }

    public static void reset(Context context) {
        AppDatabase db = get(context);
        db.clearAllTables();
    }
}
