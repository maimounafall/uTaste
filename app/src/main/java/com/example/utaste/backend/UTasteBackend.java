package com.example.utaste.backend;

import java.util.List;

public class UTasteBackend {

    private UserManager userManager;
    private SessionManager sessionManager;
    private AdminService adminService;
    private final AppDatabase db;

    public UTasteBackend(AppDatabase db) {
        this.db = db;
        this.userManager = new UserManager(db);

        // FIX: do NOT recreate session manager if already exists
        if (this.sessionManager == null) {
            this.sessionManager = new SessionManager(userManager);
        }

        this.adminService = new AdminService(db, new SimplePasswordHasher());
    }

    // ===== Authentication =====
    public boolean login(String email, String password) {
        return sessionManager.login(email, password);
    }

    public void logout(User currentUser) {
        sessionManager.logout();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    public void setCurrentUser(User user) {
        sessionManager.setCurrentUser(user);
    }

    public Role getCurrentUserRole() {
        return sessionManager.getCurrentUserRole();
    }

    // ===== Password management =====
    public void changeOwnPassword(String currentPassword, String newPassword) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");
        if (!currentUser.getPassword().equals(currentPassword))
            throw new IllegalArgumentException("Mot de passe actuel incorrect.");
        if (!PermissionManager.canChangeOwnPassword(currentUser))
            throw new SecurityException("Permission refusée.");

        userManager.changePassword(currentUser, newPassword);
    }

    public void resetUserPassword(String userEmail, String newPassword) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");
        User targetUser = userManager.getUserByEmail(userEmail);
        if (targetUser == null)
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        if (!PermissionManager.canResetPassword(currentUser, targetUser))
            throw new SecurityException("Permission refusée pour réinitialiser le mot de passe.");

        userManager.resetPassword(userEmail, newPassword);
    }

    // ===== User management =====
    public Waiter createWaiter(String firstName, String lastName, String email) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");
        if (!PermissionManager.canCreateWaiters(currentUser))
            throw new SecurityException("Permission refusée pour créer des serveurs.");

        return userManager.createWaiter(firstName, lastName, email);
    }

    public void deleteUser(String userEmail) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");

        User targetUser = userManager.getUserByEmail(userEmail);

        if (targetUser == null)
            throw new IllegalArgumentException("Utilisateur non trouvé.");

        if (!PermissionManager.canDeleteUser(currentUser, targetUser))
            throw new SecurityException("Permission refusée pour supprimer cet utilisateur.");

        userManager.removeUser(userEmail);
    }

    public void updateUserProfile(String targetEmail, String firstName, String lastName, String newEmail) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");

        User targetUser = userManager.getUserByEmail(targetEmail);
        if (targetUser == null)
            throw new IllegalArgumentException("Utilisateur non trouvé.");

        if (!PermissionManager.canModifyUser(currentUser, targetUser))
            throw new SecurityException("Permission refusée pour modifier cet utilisateur.");

        if (firstName != null) targetUser.setFirstName(firstName);
        if (lastName != null) targetUser.setLastName(lastName);
        if (newEmail != null && !newEmail.equals(targetUser.getEmail()))
            targetUser.setEmail(newEmail);

        userManager.updateUser(targetUser);
    }

    public List<Waiter> getAllWaiters() {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");
        if (!PermissionManager.canManageUsers(currentUser))
            throw new SecurityException("Permission refusée pour voir la liste des serveurs.");

        return userManager.getAllWaiters();
    }

    public User getUserByEmail(String email) {
        User currentUser = getCurrentUser();
        if (currentUser == null)
            throw new IllegalStateException("Aucun utilisateur connecté.");

        User targetUser = userManager.getUserByEmail(email);

        if (targetUser != null &&
                (currentUser.getEmail().equalsIgnoreCase(targetUser.getEmail())
                        || PermissionManager.canManageUsers(currentUser))) {
            return targetUser;
        }

        throw new SecurityException("Permission refusée pour accéder aux informations de cet utilisateur.");
    }

    public boolean emailExists(String email) {
        return userManager.emailExists(email);
    }

    public String getCurrentUserPermissions() {
        return PermissionManager.getUserPermissions(getCurrentUser());
    }

    public void refreshSession() {
        sessionManager.refreshSession();
    }

    public long getRemainingSessionTime() {
        return sessionManager.getRemainingSessionTime();
    }

    // ===== Reset database =====
    public void resetDatabase() {
        new Thread(() -> {
            try {
                db.runInTransaction(() -> {
                    db.recipeIngredientDao().deleteAll();
                    db.recipeDao().deleteAll();
                    db.ingredientDao().deleteAll();
                    db.userDao().deleteAll();
                });

                this.userManager = new UserManager(db);

                // FIX: preserve existing sessionManager
                if (this.sessionManager == null)
                    this.sessionManager = new SessionManager(userManager);

                User admin = userManager.getUserByEmail(UserManager.DEFAULT_ADMIN_EMAIL);
                if (admin != null)
                    sessionManager.setCurrentUser(admin);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    protected SessionManager getSessionManager() {
        return sessionManager;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public AppDatabase getDatabase() {
        return db;
    }
}
