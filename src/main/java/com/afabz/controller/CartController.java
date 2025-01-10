package com.afabz.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afabz.entity.User;
import com.afabz.repository.UserRepository;
import com.afabz.service.CartService;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {
   
	@Autowired
	private CartService cartService;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/items/count")
	public ResponseEntity<Integer> getCartItemCount(@RequestParam String username){
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));	
	
		int count = cartService.getCartItemCount(user.getUserId());
		return ResponseEntity.ok(count);
	}
	
	@PostMapping("/add")
	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		// Handle quantity Default to 1 if not provided
		int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
		
		//Fetch the user useing username
		
		User user = userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("User not found with username: " + username));
		
		// Add the product to the cart
		 cartService.addToCart(user.getUserId(), productId, quantity);
		 return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
