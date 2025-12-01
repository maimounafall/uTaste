// Interface for login and authentication stuff
package com.example.utaste.backend;

/**
 * Interface for authentication - we can implement this different ways
 * like with memory, database, etc.
 */
public interface IAuthenticationService {
    
    // Try to login a user
    User authenticate(String email, String password);
    
    // Check if user session is still good
    boolean validateSession(User user);
    
    // Change user password
    boolean changePassword(User user, String oldPassword, String newPassword);
    
    // Check if user can do something
    boolean hasPermission(User user, String action);
    
    // Reset password (admin only)
    boolean resetPassword(User adminUser, String targetUserEmail, String newPassword);
    
    // Log out user
    void logout(User user);
    
    // Get session timeout
    int getSessionTimeout();
    
    // Set session timeout  
    void setSessionTimeout(int timeoutMinutes);
}