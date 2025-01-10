package com.afabz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afabz.entity.CartItem;
import com.afabz.entity.Product;
import com.afabz.entity.User;
import com.afabz.repository.CartRepository;
import com.afabz.repository.ProductRepository;
import com.afabz.repository.UserRepository;

@Service
public class CartService {
 
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;

	// Get the total cart item count for  a user
   public int getCartItemCount(int userId) {
	   User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
	   return cartRepository.countTotalItems(user);
	   
   }
   
   // Add an item to the cart
   public void addToCart(int userId, int productId, int quantity) {
	   User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
	   
	   Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
	   
	   Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(user, product);
	   
	   if (existingItem.isPresent()) {
		   CartItem cartItem = existingItem.get();
		   cartItem.setQuantity(cartItem.getQuantity() + quantity);
		   cartRepository.save(cartItem);
	   } else {
		   CartItem newItem = new CartItem(user, product, quantity);
		   cartRepository.save(newItem);
		   
	   }
   }
}
