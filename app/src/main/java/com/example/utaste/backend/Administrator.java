
// Class for administrator users
package com.example.utaste.backend;

/**
 * Administrator class - inherits from User
 * Admins can manage waiters (create/modify/delete)
 * This corresponds to the "Administrateur" class from our UML diagram
 */
public class Administrator extends User {
    
    /**
     * Constructor - creates a new administrator
     * Calls the parent class constructor (User)
     */
    public Administrator(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        this.role = Role.ADMINISTRATOR;
    }


    /**
     * Required implementation of User's abstract method
     * Administrators have the ADMINISTRATOR role
     */
    @Override
    public Role getRole() {
        return Role.ADMINISTRATOR;
    }
    
    // Note: methods like createWaiterUser() are in UTasteBackend
    // because we use the Facade pattern instead of putting business logic here
}
