// Converters.java
package com.example.utaste.backend;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static String fromRole(Role role) {
        if (role == null) {
            return null;
        } else {
            return role.name(); // ADMINISTRATOR / CHEF / WAITER
        }
    }

    @TypeConverter
    public static Role toRole(String value) {
        if (value == null) {
            return null;
        } else {
            // On essaie de convertir, si erreur on retourne WAITER par défaut
            try {
                return Role.valueOf(value);
            } catch (IllegalArgumentException ex) {
                return Role.WAITER;
            }
        }
    }
}

