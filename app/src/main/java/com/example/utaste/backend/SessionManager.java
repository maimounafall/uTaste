package com.example.utaste.backend;

import java.time.LocalDateTime;

/**
 * FIXED + COMPLETE SessionManager
 * - no unwanted session expiration
 * - fully compatible with UTasteBackend
 * - implements all interface methods
 * - keeps refreshSession + getRemainingSessionTime
 */
public class SessionManager implements IAuthenticationService {

    private User currentUser;
    private LocalDateTime loginTime;
    private static UserManager userManager;

    // Infinite session (no auto logout)
    private static final int DEFAULT_SESSION_TIMEOUT_MINUTES = Integer.MAX_VALUE;
    private int sessionTimeoutMinutes = DEFAULT_SESSION_TIMEOUT_MINUTES;

    public SessionManager(UserManager manager) {
        userManager = manager;
        currentUser = null;
        loginTime = null;
    }

    // ---------------- AUTHENTICATION ----------------

    @Override
    public User authenticate(String email, String password) {
        return userManager.authenticate(email, password);
    }

    public boolean login(String email, String password) {
        User user = authenticate(email, password);
        if (user == null)
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");

        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        return true;
    }

    // ---------------- LOGOUT ----------------

    @Override
    public void logout(User user) {
        if (currentUser != null &&
                user != null &&
                currentUser.getEmail().equalsIgnoreCase(user.getEmail())) {

            this.currentUser = null;
            this.loginTime = null;
        }
    }

    public void logout() {
        this.currentUser = null;
        this.loginTime = null;
    }

    // ---------------- SESSION STATUS ----------------

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    @Override
    public boolean validateSession(User user) {
        if (user == null || currentUser == null) return false;
        return user.getEmail().equalsIgnoreCase(currentUser.getEmail());
    }

    public void refreshSession() {
        if (currentUser != null)
            loginTime = LocalDateTime.now();
    }

    public long getRemainingSessionTime() {
        if (currentUser == null || loginTime == null) return 0;

        LocalDateTime expire = loginTime.plusMinutes(sessionTimeoutMinutes);
        return java.time.Duration.between(LocalDateTime.now(), expire).toMinutes();
    }

    // ---------------- GETTERS ----------------

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
    }

    public Role getCurrentUserRole() {
        return (currentUser != null) ? currentUser.getRole() : null;
    }

    // ---------------- PERMISSIONS (INTERFACE) ----------------

    @Override
    public boolean hasPermission(User user, String action) {
        if (user == null) return false;
        return PermissionManager.getUserPermissions(user).contains(action);
    }

    // ---------------- PASSWORD MGMT ----------------

    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!validateSession(user))
            throw new SecurityException("Utilisateur non connecté.");

        if (!user.getPassword().equals(oldPassword))
            throw new SecurityException("Mot de passe actuel incorrect.");

        userManager.changePassword(user, newPassword);
        return true;
    }

    @Override
    public boolean resetPassword(User adminUser, String targetUserEmail, String newPassword) {
        User target = userManager.getUserByEmail(targetUserEmail);
        if (!PermissionManager.canResetPassword(adminUser, target))
            throw new SecurityException("Permission refusée pour réinitialiser le mot de passe.");

        userManager.resetPassword(targetUserEmail, newPassword);
        return true;
    }

    // ---------------- TIMEOUT SETTINGS ----------------

    @Override
    public int getSessionTimeout() {
        return sessionTimeoutMinutes;
    }

    @Override
    public void setSessionTimeout(int timeoutMinutes) {
        this.sessionTimeoutMinutes = timeoutMinutes;
    }
}
