// Interface for managing users
package com.example.utaste.backend;

import java.util.List;

/**
 * Interface for user management - can be implemented with memory, database, etc.
 */
public interface IUserRepository {
    
    // Add new user
    void addUser(User user);
    
    // Remove user
    boolean removeUser(String email);
    
    // Update user info
    void updateUser(User user);
    
    /**
     * Find a user by email address
     * @param email The user's email
     * @return User object if found, null otherwise
     */
    User getUserByEmail(String email);
    
    /**
     * Check if an email already exists in the system
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
    
    /**
     * Get all users with a specific role
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    List<User> getUsersByRole(Role role);
    
    /**
     * Get all users in the system
     * @return List of all users
     */
    List<User> getAllUsers();
    
    /**
     * Get the total number of users in the system
     * @return Total user count
     */
    int getUserCount();
    
    /**
     * Get the number of users with a specific role
     * @param role The role to count
     * @return Number of users with the specified role
     */
    int getUserCountByRole(Role role);
    
    /**
     * Clear all users from the system (reset operation)
     * This should preserve default users (admin, chef)
     */
    void resetToDefaults();
    
    /**
     * Validate user data according to business rules
     * @param user The user to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validateUserData(User user);
}