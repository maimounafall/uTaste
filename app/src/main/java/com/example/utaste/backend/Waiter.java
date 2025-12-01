package com.example.utaste.backend;

/**
 * Classe Waiter - représente un serveur.
 * Les serveurs sont créés par l'administrateur avec un mot de passe par défaut.
 */
public class Waiter extends User {

    /**
     * Constructeur du serveur.
     */
    public Waiter(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.WAITER);
    }

    /**
     * Tous les serveurs ont le rôle WAITER.
     */
    @Override
    public Role getRole() {
        this.role = Role.WAITER;
        return Role.WAITER;
    }
}

