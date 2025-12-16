package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void get_items() {
        Item item = new Item();
        item.setName("Shoe");
        item.setPrice(BigDecimal.valueOf(79.99));

        List<Item> items = List.of(item);

        when(itemRepo.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Shoe", response.getBody().getFirst().getName());
        verify(itemRepo, times(1)).findAll();
    }

    @Test
    public void get_item_by_id() {
        Item item = new Item();
        item.setId(1L);

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(itemRepo).findById(1L);
    }

    @Test
    public void get_items_by_name_found() {
        Item item = new Item();
        item.setName("Shoe");

        when(itemRepo.findByName("Shoe")).thenReturn(List.of(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Shoe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Shoe", response.getBody().getFirst().getName());
        verify(itemRepo).findByName("Shoe");
    }

    @Test
    public void get_items_by_name_bad_request() {
        // test case : null
        when(itemRepo.findByName(any())).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Shoe");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // test case: empty list
        when(itemRepo.findByName(any())).thenReturn(List.of());

        response = itemController.getItemsByName("Shoe");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
