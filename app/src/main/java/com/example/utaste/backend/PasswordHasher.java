package com.example.utaste.backend;


public interface PasswordHasher {
    String hash(String plain);
    boolean verify(String plain, String hash);
}