package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submit_order_happy_path() {
        User user = new User();
        user.setUsername("test");

        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(10));

        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);

        when(userRepo.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getItems().size());
        assertEquals(BigDecimal.valueOf(30), response.getBody().getTotal());
        assertEquals("test", response.getBody().getUser().getUsername());
        verify(orderRepo).save(any(UserOrder.class));
    }

    @Test
    public void submit_user_not_found() {
        when(userRepo.findByUsername("missing")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(orderRepo);
    }

    @Test
    public void get_orders_for_user_happy_path() {
        User user = new User();
        user.setUsername("test");

        UserOrder order1 = new UserOrder();
        UserOrder order2 = new UserOrder();

        List<UserOrder> orders = List.of(order1, order2);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(userRepo).findByUsername("test");
        verify(orderRepo).findByUser(user);
    }

    @Test
    public void get_orders_for_user_user_not_found() {
        when(userRepo.findByUsername("missing")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepo).findByUsername("missing");
        verifyNoInteractions(orderRepo);
    }
}
