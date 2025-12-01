package com.example.utaste.backend;

import java.time.LocalDateTime;

/**
 * Classe abstraite User - classe mère de tous les utilisateurs.
 * Administrator, Chef et Waiter héritent de cette classe.
 */
public abstract class User {

    // Champs protégés accessibles aux sous-classes
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected Role role;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    /**
     * Constructeur complet avec rôle explicite.
     */
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructeur sans rôle (rarement utilisé).
     */
    public User(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, null);
    }

    /**
     * Retourne le rôle de l'utilisateur.
     * S'assure qu'il n'est jamais null.
     */
    public Role getRole() {
        if (this.role == null) {
            if (this instanceof Administrator) this.role = Role.ADMINISTRATOR;
            else if (this instanceof Chef) this.role = Role.CHEF;
            else if (this instanceof Waiter) this.role = Role.WAITER;
        }
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
        updateTimestamp();
    }

    // --- Getters & setters classiques ---
    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateTimestamp();
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateTimestamp();
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
        updateTimestamp();
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // --- Conversion DB → Java ---
    public void setCreatedAt(long tsMillis) {
        this.createdAt = java.time.Instant.ofEpochMilli(tsMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public void setUpdatedAt(long tsMillis) {
        this.updatedAt = java.time.Instant.ofEpochMilli(tsMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // --- Conversion Java to DB ---
    public long getCreatedAtMillis() {
        return (createdAt != null)
                ? createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                : System.currentTimeMillis();
    }

    public long getUpdatedAtMillis() {
        return (updatedAt != null)
                ? updatedAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                : System.currentTimeMillis();
    }

    /**
     * Met à jour updatedAt à maintenant.
     */
    protected void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
