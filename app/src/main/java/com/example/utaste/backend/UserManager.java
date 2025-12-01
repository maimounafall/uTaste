package com.example.utaste.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * FINAL CLEAN VERSION OF USERMANAGER
 * - updateUser fixed
 * - createdAt / updatedAt consistent with DB
 * - no renaming, no extra methods
 * - fully compatible with UTasteBackend
 */
public class UserManager implements IUserRepository {

    private Map<String, User> users = new HashMap<>();

    public static final String DEFAULT_ADMIN_EMAIL = "admin@utaste.ca";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin-pwd";
    public static final String DEFAULT_CHEF_EMAIL = "chef@utaste.ca";
    public static final String DEFAULT_CHEF_PASSWORD = "chef-pwd";
    public static final String DEFAULT_WAITER_PASSWORD = "waiter-pwd";

    private final UserDao userDao;

    public UserManager(AppDatabase db) {
        this.userDao = db.userDao();
        this.users = new HashMap<>();

        List<UserEntity> entities = userDao.findAll();

        if (entities.isEmpty()) {
            // Create + save default admin & chef
            Administrator admin = new Administrator("Admin", "", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
            Chef chef = new Chef("Chef", "", DEFAULT_CHEF_EMAIL, DEFAULT_CHEF_PASSWORD);

            addUserInternal(admin, true);
            addUserInternal(chef, true);

        } else {
            // Rebuild in-memory map
            for (UserEntity e : entities) {
                User u = mapToDomainUser(e);
                users.put(u.getEmail().toLowerCase(), u);
            }
        }
    }

    // ---------------------------------
    // MAP DB ENTITY → DOMAIN MODEL
    // ---------------------------------
    private User mapToDomainUser(UserEntity e) {
        String pwd = e.passwordHash;

        User u;
        if (e.role == Role.ADMINISTRATOR) {
            u = new Administrator(e.firstName, e.lastName, e.email, pwd);
        } else if (e.role == Role.CHEF) {
            u = new Chef(e.firstName, e.lastName, e.email, pwd);
        } else {
            u = new Waiter(e.firstName, e.lastName, e.email, pwd);
        }

        // sync timestamps
        u.setCreatedAt(e.createdAt);
        u.setUpdatedAt(e.updatedAt);

        return u;
    }

    private void addUserInternal(User user, boolean persist) {
        validateUser(user);
        String key = user.getEmail().toLowerCase();

        if (users.containsKey(key))
            throw new IllegalArgumentException("Email already used.");

        users.put(key, user);

        if (persist) {
            saveUserToDb(user);
        }
    }

    // ---------------------------------
    // SAVE USER TO DB (INSERT OR UPDATE)
    // ---------------------------------
    private void saveUserToDb(User user) {
        UserEntity existing = userDao.findByEmail(user.getEmail());
        UserEntity e = (existing != null) ? existing : new UserEntity();

        e.email = user.getEmail();
        e.firstName = user.getFirstName();
        e.lastName = user.getLastName();
        e.role = user.getRole();
        e.passwordHash = user.getPassword();

        long now = System.currentTimeMillis();

        if (existing == null) {
            e.createdAt = now;
            e.updatedAt = now;
            long id = userDao.insert(e);
        } else {
            e.updatedAt = now;
            userDao.update(e);
        }

        // sync timestamps back to the domain object
        user.setCreatedAt(e.createdAt);
        user.setUpdatedAt(e.updatedAt);
    }

    // ---------------------------------
    // PUBLIC METHODS
    // ---------------------------------
    public void addUser(User user) {
        addUserInternal(user, true);
    }

    private void validateUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null.");

        if (!DataValidator.isValidEmail(user.getEmail()))
            throw new IllegalArgumentException(DataValidator.getEmailError(user.getEmail()));

        if (!DataValidator.isValidPassword(user.getPassword()))
            throw new IllegalArgumentException(DataValidator.getPasswordError(user.getPassword()));

        if (user.getFirstName() != null &&
                !user.getFirstName().trim().isEmpty() &&
                !DataValidator.isValidName(user.getFirstName()))
            throw new IllegalArgumentException("Invalid first name.");

        if (user.getLastName() != null &&
                !user.getLastName().trim().isEmpty() &&
                !DataValidator.isValidName(user.getLastName()))
            throw new IllegalArgumentException("Invalid last name.");
    }

    @Override
    public boolean removeUser(String email) {
        if (email == null) return false;

        String key = email.toLowerCase();
        User removed = users.remove(key);

        if (removed != null) {
            UserEntity ue = userDao.findByEmail(email);
            if (ue != null) userDao.deleteById(ue.id);
            return true;
        }
        return false;
    }

    // ---------------------------------
    // CREATE WAITER
    // ---------------------------------
    public Waiter createWaiter(String firstName, String lastName, String email) {
        if (!DataValidator.isValidEmail(email))
            throw new IllegalArgumentException(DataValidator.getEmailError(email));

        String key = email.toLowerCase();
        if (users.containsKey(key))
            throw new IllegalArgumentException("Email already used.");

        Waiter w = new Waiter(firstName, lastName, email, DEFAULT_WAITER_PASSWORD);
        addUserInternal(w, true);

        return w;
    }

    public List<Waiter> getAllWaiters() {
        List<Waiter> out = new ArrayList<>();
        for (User u : users.values()) {
            if (u instanceof Waiter)
                out.add((Waiter) u);
        }
        return out;
    }

    // ---------------------------------
    //  updateUser()
    // ---------------------------------
    public void updateUser(User user) {
        validateUser(user);

        long now = System.currentTimeMillis();
        user.setUpdatedAt(now);

        users.put(user.getEmail().toLowerCase(), user);

        UserEntity entity = userDao.findByEmail(user.getEmail());
        if (entity != null) {
            entity.firstName = user.getFirstName();
            entity.lastName = user.getLastName();
            entity.email = user.getEmail();
            entity.updatedAt = now;
            userDao.update(entity);
        }
    }

    // ---------------------------------
    // AUTHENTICATE
    // ---------------------------------
    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email required.");
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password required.");

        String key = email.trim().toLowerCase();

        User user = users.get(key);

        if (user == null) {
            UserEntity entity = userDao.findByEmail(email);
            if (entity != null) {
                user = mapToDomainUser(entity);
                users.put(key, user);
            }
        }

        if (user != null && user.getPassword().equals(password))
            return user;

        return null;
    }

    // ---------------------------------
    // PASSWORD CHANGE
    // ---------------------------------
    public void changePassword(User user, String newPassword) {
        if (user == null)
            throw new IllegalArgumentException("User not found");

        if (!DataValidator.isValidPassword(newPassword))
            throw new IllegalArgumentException(DataValidator.getPasswordError(newPassword));

        long now = System.currentTimeMillis();

        // Update domain object
        user.setPassword(newPassword);
        user.setUpdatedAt(now);

        // Update memory
        users.put(user.getEmail().toLowerCase(), user);

        // Update DB
        UserEntity entity = userDao.findByEmail(user.getEmail());
        if (entity != null) {
            entity.passwordHash = newPassword;
            entity.updatedAt = now;
            userDao.update(entity);
        }
    }


    public void resetPassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        if (user == null)
            throw new IllegalArgumentException("User not found");

        changePassword(user, newPassword);
    }

    // ---------------------------------
    // GET USER
    // ---------------------------------
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;

        String key = email.trim().toLowerCase();

        User u = users.get(key);
        if (u != null) return u;

        UserEntity entity = userDao.findByEmail(email);
        if (entity != null) {
            User mapped = mapToDomainUser(entity);
            users.put(key, mapped);
            return mapped;
        }

        return null;
    }

    public boolean emailExists(String email) {
        if (email == null) return false;
        String key = email.trim().toLowerCase();
        return users.containsKey(key) || userDao.findByEmail(email) != null;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    // ---------------------------------
    // INTERFACE METHODS
    // ---------------------------------
    @Override
    public List<User> getUsersByRole(Role role) {
        List<User> out = new ArrayList<>();
        for (User u : users.values()) {
            if (u.getRole() == role)
                out.add(u);
        }
        return out;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int getUserCount() {
        return users.size();
    }

    @Override
    public int getUserCountByRole(Role role) {
        return getUsersByRole(role).size();
    }

    @Override
    public void resetToDefaults() {
        users.clear();

        for (UserEntity e : userDao.findAll())
            userDao.deleteById(e.id);

        Administrator admin = new Administrator("Admin", "", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
        Chef chef = new Chef("Chef", "", DEFAULT_CHEF_EMAIL, DEFAULT_CHEF_PASSWORD);

        addUserInternal(admin, true);
        addUserInternal(chef, true);
    }

    @Override
    public void validateUserData(User user) {
        validateUser(user);
    }
}
