package com.example.utaste;

import com.example.utaste.InMemoryDbFactory;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.Role;
import com.example.utaste.backend.UserEntity;
import com.example.utaste.backend.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(sdk = 33)
public class UserServiceTest {

    private UserService service;
    private AppDatabase db;

    @Before
    public void setup() {
        db = InMemoryDbFactory.create();
        service = new UserService(db);
    }

    // 1. Vendor creation works
    @Test
    public void createVendor_shouldCreateUser() {
        long id = service.createVendor("Anna", "anna@mail.com");
        assertTrue(id > 0);
    }

    // 2. Vendor can be retrieved
    @Test
    public void getVendor_shouldReturnCorrectUser() {
        long id = service.createVendor("John", "john@mail.com");
        UserEntity user = service.getVendor(id);

        assertNotNull(user);
        assertEquals("John", user.firstName);
        assertEquals("john@mail.com", user.email);
    }

    // 3. Vendor has WAITER role
    @Test
    public void createVendor_shouldAssignWaiterRole() {
        long id = service.createVendor("Alex", "alex@mail.com");
        UserEntity user = service.getVendor(id);

        assertEquals(Role.WAITER, user.role);
    }

    // 4. All vendors list contains the created vendor
    @Test
    public void getAllVendors_shouldContainCreatedVendor() {
        service.createVendor("Zoe", "zoe@mail.com");

        List<UserEntity> vendors = service.getAllVendors();
        assertFalse(vendors.isEmpty());
        assertEquals(Role.WAITER, vendors.get(0).role);
    }

    // 5. Email must be unique
    @Test(expected = Exception.class)
    public void createVendor_shouldFailIfEmailExists() {
        service.createVendor("Bob", "bob@mail.com");
        service.createVendor("DuplicateBob", "bob@mail.com"); // must throw
    }

    // 6. Vendor deletion removes entry
    @Test
    public void deleteVendor_shouldRemoveUser() {
        long id = service.createVendor("Sara", "sara@mail.com");

        service.deleteVendor(id);
        UserEntity user = service.getVendor(id);

        assertNull(user);
    }

    // 7. createdAt timestamp must be set
    @Test
    public void createVendor_setsCreatedAtTimestamp() {
        long id = service.createVendor("Time", "time@mail.com");
        UserEntity user = service.getVendor(id);

        assertTrue(user.createdAt > 0);
    }

    // 8. updatedAt timestamp must be set
    @Test
    public void createVendor_setsUpdatedAtTimestamp() {
        long id = service.createVendor("Tim", "tim@mail.com");
        UserEntity user = service.getVendor(id);

        assertTrue(user.updatedAt > 0);
    }

    // 9. passwordHash is initialized (default)
    @Test
    public void createVendor_initializesPasswordHash() {
        long id = service.createVendor("Emma", "emma@mail.com");
        UserEntity user = service.getVendor(id);

        assertNotNull(user.passwordHash);
    }

    // 10. getAllVendors returns correct number of vendors
    @Test
    public void getAllVendors_returnsCorrectCount() {
        service.createVendor("A", "a@mail.com");
        service.createVendor("B", "b@mail.com");

        List<UserEntity> vendors = service.getAllVendors();
        assertEquals(2, vendors.size());
    }
}
