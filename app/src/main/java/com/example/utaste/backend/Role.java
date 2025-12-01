
// Enumeration for user roles
package com.example.utaste.backend;

/**
 * Role enum - defines the 3 types of users in our system
 * Used by getRole() in each User class to identify the type
 * Also used by PermissionManager to check permissions
 */
public enum Role {
    ADMINISTRATOR, // Admin user - can manage waiters
    CHEF,          // Chef user - can manage recipes  
    WAITER         // Waiter user - can view recipes and manage sales
    
    // Note: we could have added methods here like getPermissions()
    // but we preferred to put that in PermissionManager to separate responsibilities
}
