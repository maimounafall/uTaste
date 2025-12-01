
// Class for chef users
package com.example.utaste.backend;

/**
 * Chef class - inherits from User
 * Chefs will be able to manage recipes (in future deliverables)
 * For now they can only change their password
 */
public class Chef extends User {
    
    /**
     * Constructor - creates a new chef
     * The default chef has email "chef@utaste.ca" and password "chef-pwd"
     */
    public Chef(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.role = Role.CHEF;
    }


    /**
     * Required implementation - chefs have the CHEF role
     * Used by PermissionManager to check permissions
     */
    @Override
    public Role getRole() {
        return Role.CHEF;
    }
    
    // Note: the UML methods viewRecipe() and modifyRecipe() will be
    // implemented in deliverables 2-3 for recipe management
}
