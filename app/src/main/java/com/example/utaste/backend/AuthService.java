
package com.example.utaste.backend;

public class AuthService {

    private final UserDao users;
    private final PasswordHasher hasher;

    public AuthService(UserDao users, PasswordHasher hasher) {
        this.users = users;
        this.hasher = hasher;
    }

    public UserEntity authenticate(String email, String plainPwd) {
        UserEntity u = users.findByEmail(email);
        if (u == null) {
            throw new IllegalArgumentException("Unknown user.");
        } else {
            boolean ok = hasher.verify(plainPwd, u.passwordHash);
            if (ok) {
                return u;
            } else {
                throw new SecurityException("Invalid password.");
            }
        }
    }

    public void changeOwnPassword(long userId, String oldPwd, String newPwd) {
        UserEntity u = users.findById(userId);
        if (u == null) {
            throw new IllegalArgumentException("User not found.");
        } else {
            boolean ok = hasher.verify(oldPwd, u.passwordHash);
            if (ok) {
                u.passwordHash = hasher.hash(newPwd);
                u.isDefaultPwd = false;
                u.updatedAt = System.currentTimeMillis();
                users.update(u);
            } else {
                throw new SecurityException("Invalid old password.");
            }
        }
    }
}
