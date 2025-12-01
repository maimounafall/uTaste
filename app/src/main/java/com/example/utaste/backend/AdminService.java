
package com.example.utaste.backend;

public class AdminService {

    private final AppDatabase db;
    private final UserDao users;
    private final PasswordHasher hasher;

    public AdminService(AppDatabase db, PasswordHasher hasher) {
        this.db = db;
        this.users = db.userDao();
        this.hasher = hasher;
    }

    public void resetDatabase() {
        db.runInTransaction(() -> {
            // Note: Les ventes seront implémentées dans un futur livrable
            db.recipeIngredientDao().deleteAll();  // Supprimer liaisons recette-ingrédient
            db.recipeDao().deleteAll();            // Supprimer toutes les recettes
            db.ingredientDao().deleteAll();        // Supprimer tous les ingrédients
            users.deleteAllWaiters();              // Supprimer tous les serveurs
        });
    }

    public void resetPasswordToDefault(long userId, String defaultPlainPassword) {
        String defaultHash = hasher.hash(defaultPlainPassword);
        long ts = System.currentTimeMillis();
        users.resetPassword(userId, defaultHash, ts);
    }

    public void updateProfile(long userId, String first, String last, String email) {
        UserEntity u = users.findById(userId);
        if (u == null) {
            throw new IllegalArgumentException("User not found.");
        } else {
            u.firstName = first;
            u.lastName = last;
            u.email = email;
            u.updatedAt = System.currentTimeMillis();
            users.update(u);
        }
    }
}
