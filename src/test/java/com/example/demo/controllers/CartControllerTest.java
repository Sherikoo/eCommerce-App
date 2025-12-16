package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void add_to_cart_happy_path() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(10));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(BigDecimal.valueOf(20), response.getBody().getTotal());

        verify(userRepo).findByUsername("test");
        verify(itemRepo).findById(1L);
        verify(cartRepo).save(cart);
    }

    @Test
    public void add_to_cart_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("missing");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("missing")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepo).findByUsername("missing");
        verifyNoInteractions(itemRepo, cartRepo);
    }

    @Test
    public void add_to_cart_item_not_found() {
        User user = new User();
        user.setUsername("test");
        user.setCart(new Cart());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(99L);
        request.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepo).findByUsername("test");
        verify(itemRepo).findById(99L);
        verifyNoInteractions(cartRepo);
    }

    @Test
    public void remove_from_cart_happy_path() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(25));

        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(BigDecimal.valueOf(50), response.getBody().getTotal());

        verify(cartRepo).save(cart);
    }

    @Test
    public void remove_from_cart_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("missing");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("missing")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(itemRepo, cartRepo);
    }

    @Test
    public void remove_from_cart_item_not_found() {
        User user = new User();
        user.setUsername("test");
        user.setCart(new Cart());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(cartRepo);
    }
}
