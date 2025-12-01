package com.example.utaste.backend;


import java.util.Objects;


public class SimplePasswordHasher implements PasswordHasher {


@Override
public String hash(String plain) {
    if (plain == null) {
        return "";
    } else {
        // Implémentation simple pour un projet étudiant (non sécurisée pour prod)
        // Ajout d'un "salt" basique pour plus de sécurité
        String salt = "uTaste2025";
        return "HASH_" + salt + "_" + plain + "_END";
    }
}


@Override
public boolean verify(String plain, String hash) {
    if (hash == null) {
        return false;
    } else {
        String expected = hash(plain);
        return Objects.equals(expected, hash);
    }
}
}