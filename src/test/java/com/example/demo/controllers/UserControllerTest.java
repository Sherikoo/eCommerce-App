package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void create_user_bad_requests() {
        // test case: password != confirmPassword
        CreateUserRequest r1 = new CreateUserRequest();
        r1.setUsername("test");
        r1.setPassword("test");
        r1.setConfirmPassword("testtest");

        ResponseEntity<User> response = userController.createUser(r1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // test case: null password
        CreateUserRequest r2 = new CreateUserRequest();
        r2.setUsername("test");

        response = userController.createUser(r2);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // test case: password length < 7 characters
        CreateUserRequest r3 = new CreateUserRequest();
        r3.setUsername("test");
        r3.setPassword("test");
        r3.setConfirmPassword("test");

        response = userController.createUser(r3);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void find_by_username_found() {
        User user = new User();
        user.setUsername("test");

        when(userRepo.findByUsername("test")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().getUsername());
    }

    @Test
    public void find_by_username_not_found() {
        when(userRepo.findByUsername("missing")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void find_by_id_found() {
        User user = new User();
        user.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }
}
