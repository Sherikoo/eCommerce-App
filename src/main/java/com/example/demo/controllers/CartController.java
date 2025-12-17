package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {

		log.info("AddToCart request username={} itemId={} quantity={}",
				request.getUsername(), request.getItemId(), request.getQuantity());

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.warn("AddToCart FAILED reason=user_not_found username={}", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(item.isEmpty()) {
			log.warn("AddToCart FAILED reason=item_not_found itemId={}", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		cartRepository.save(cart);
		log.info("AddToCart SUCCESS username={} itemId={} quantity={}",
				request.getUsername(), request.getItemId(), request.getQuantity());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {

		log.info("RemoveFromCart request username={} itemId={} quantity={}",
				request.getUsername(), request.getItemId(), request.getQuantity());

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.warn("RemoveFromCart FAILED reason=user_not_found username={}", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(item.isEmpty()) {
			log.warn("RemoveFromCart FAILED reason=item_not_found itemId={}", request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		cartRepository.save(cart);
		log.info("RemoveFromCart SUCCESS username={} itemId={} quantity={}",
				request.getUsername(), request.getItemId(), request.getQuantity());
		return ResponseEntity.ok(cart);
	}
}
