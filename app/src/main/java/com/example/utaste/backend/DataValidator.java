// Simple class to check if data is valid
package com.example.utaste.backend;

import java.util.regex.Pattern;

/**
 * This class helps validate user data like emails and passwords
 * @author Student Team
 */
public class DataValidator {
    
    // Pattern to check email format - found this on Stack Overflow
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Password should be at least 8 chars
    private static final int MIN_PASSWORD_LENGTH = 8;
    
    /**
     * Check if an email has the right format
     * Uses our compiled regex Pattern for performance
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;  // email is required
        }
        return EMAIL_PATTERN.matcher(email).matches();  // uses the regex
    }
    
    /**
     * Check that the password is long enough
     * Minimum 8 characters as requested in requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.length() >= MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Check that a string is not empty (general utility)
     */
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Check that a name contains only allowed characters
     * We accept letters, spaces, hyphens and apostrophes (for French names)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;  // but names can be optional depending on context
        }
        // Regex: letters (a-z), accents (À-ÿ), spaces (\s), hyphens (-) and apostrophes (')
        return name.matches("^[a-zA-ZÀ-ÿ\\s'-]+$");
    }
    
    // Get error message for email
    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "L'adresse email est obligatoire.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Format d'adresse email invalide.";
        }
        return "";
    }
    
    // Get error message for password  
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Le mot de passe est obligatoire.";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères.";
        }
        return "";
    }
}