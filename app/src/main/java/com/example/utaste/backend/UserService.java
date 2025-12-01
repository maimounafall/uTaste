package com.example.utaste.backend;

import java.util.List;

public class UserService {

    private final UserDao users;

    public UserService(AppDatabase db) {
        this.users = db.userDao();
    }

    // Create vendor using UserEntity (Room model)
    public long createVendor(String name, String email) {

        UserEntity u = new UserEntity();
        u.firstName = name;
        u.lastName = "";
        u.email = email;
        u.passwordHash = "";     // or default password
        u.role = Role.WAITER;    // ENUM value
        u.createdAt = System.currentTimeMillis();
        u.updatedAt = System.currentTimeMillis();

        return users.insert(u);
    }

    public List<UserEntity> getAllVendors() {
        return users.findByRole(Role.WAITER);
    }

    public void deleteVendor(long id) {
        users.deleteById(id);
    }

    public UserEntity getVendor(long id) {
        return users.findById(id);
    }
}
