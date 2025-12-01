// Check what users can and can't do based on their role
package com.example.utaste.backend;

/**
 * Helper class to check permissions for different user types
 */
public class PermissionManager {
    
    // Check if user can manage other users (only admins can)
    public static boolean canManageUsers(User user) {
        return user != null && user.getRole() == Role.ADMINISTRATOR;
    }
    
    // Check if user can create waiters (same as managing users)
    public static boolean canCreateWaiters(User user) {
        return canManageUsers(user);
    }
    
    /**
     * Check if a user can delete other users
     * @param user The user to check permissions for
     * @param targetUser The user to be deleted
     * @return true if user can delete the target user
     */
    public static boolean canDeleteUser(User user, User targetUser) {
        if (!canManageUsers(user) || targetUser == null) {
            return false;
        }
        
        // Admin cannot delete themselves or other admins
        if (targetUser.getRole() == Role.ADMINISTRATOR) {
            return false;
        }
        
        return true;
    }
    
    // Check if user can modify another user
    public static boolean canModifyUser(User user, User targetUser) {
        if (user == null || targetUser == null) {
            return false;
        }
        
        // You can always change your own stuff
        if (user.getEmail().equalsIgnoreCase(targetUser.getEmail())) return true;


        // Admins can change other users (but not other admins)
        if (user.getRole() == Role.ADMINISTRATOR && targetUser.getRole() != Role.ADMINISTRATOR) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if a user can reset another user's password
     * @param user The user to check permissions for
     * @param targetUser The user whose password will be reset
     * @return true if user can reset the password
     */
    public static boolean canResetPassword(User user, User targetUser) {
        if (!canManageUsers(user) || targetUser == null) {
            return false;
        }
        
        // Admin cannot reset other admin passwords
        if (targetUser.getRole() == Role.ADMINISTRATOR && !user.equals(targetUser)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if a user can change their own password
     * @param user The user to check permissions for
     * @return true if user can change their own password
     */
    public static boolean canChangeOwnPassword(User user) {
        return user != null;
    }
    
    /**
     * Check if a user can manage recipes (create, modify, delete)
     * @param user The user to check permissions for
     * @return true if user can manage recipes
     */
    public static boolean canManageRecipes(User user) {
        return user != null && user.getRole() == Role.CHEF;
    }
    
    /**
     * Check if a user can view recipes
     * @param user The user to check permissions for
     * @return true if user can view recipes
     */
    public static boolean canViewRecipes(User user) {
        if (user == null) {
            return false;
        }
        
        Role role = user.getRole();
        return role == Role.CHEF || role == Role.WAITER;
    }
    
    /**
     * Check if a user can record sales
     * @param user The user to check permissions for
     * @return true if user can record sales
     */
    public static boolean canRecordSales(User user) {
        return user != null && user.getRole() == Role.WAITER;
    }
    
    /**
     * Check if a user can view sales reports
     * @param user The user to check permissions for
     * @return true if user can view sales reports
     */
    public static boolean canViewSalesReports(User user) {
        if (user == null) {
            return false;
        }
        
        Role role = user.getRole();
        return role == Role.WAITER || role == Role.ADMINISTRATOR;
    }
    
    /**
     * Check if a user can reset the database
     * @param user The user to check permissions for
     * @return true if user can reset the database
     */
    public static boolean canResetDatabase(User user) {
        return user != null && user.getRole() == Role.ADMINISTRATOR;
    }
    
    /**
     * Check if a user can access nutritional information
     * @param user The user to check permissions for
     * @return true if user can access nutritional info
     */
    public static boolean canAccessNutritionalInfo(User user) {
        return user != null && user.getRole() == Role.CHEF;
    }
    
    /**
     * Get user's available permissions as a readable string
     * @param user The user to get permissions for
     * @return String describing user's permissions
     */
    public static String getUserPermissions(User user) {
        if (user == null) {
            return "Aucune permission (utilisateur non connecté)";
        }
        
        StringBuilder permissions = new StringBuilder();
        permissions.append("Permissions pour ").append(user.getRole()).append(":\n");
        
        switch (user.getRole()) {
            case ADMINISTRATOR:
                permissions.append("- Gérer les utilisateurs (créer, modifier, supprimer)\n");
                permissions.append("- Réinitialiser les mots de passe\n");
                permissions.append("- Réinitialiser la base de données\n");
                permissions.append("- Changer son propre mot de passe\n");
                permissions.append("- Voir les rapports de ventes\n");
                break;
                
            case CHEF:
                permissions.append("- Gérer les recettes (créer, modifier, supprimer)\n");
                permissions.append("- Accéder aux informations nutritionnelles\n");
                permissions.append("- Calculer les bilans caloriques\n");
                permissions.append("- Changer son propre mot de passe\n");
                break;
                
            case WAITER:
                permissions.append("- Voir la liste des recettes\n");
                permissions.append("- Enregistrer les ventes\n");
                permissions.append("- Voir les rapports de ventes\n");
                permissions.append("- Changer son propre mot de passe\n");
                break;
        }
        
        return permissions.toString();
    }
}