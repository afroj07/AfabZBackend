package com.afabz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afabz.entity.OrderItem;
import com.afabz.entity.Product;
import com.afabz.entity.ProductImage;
import com.afabz.entity.User;
import com.afabz.repository.OrderItemRepository;
import com.afabz.repository.ProductImageRepository;
import com.afabz.repository.ProductRepository;

@Service
public class OrderService {
 
   @Autowired
   private OrderItemRepository orderItemRepository;
   
   @Autowired
   private ProductRepository productRepository;
   
   @Autowired
   private ProductImageRepository productImageRepository;
   
   /**
    * Fetches all successful orders for a given user and returns the required response format.
    *
    * @param user The authenticated user object.
    * @return A map containing the user's role, username, and ordered products.
    */
   
   public Map<String, Object> getOrdersForUser(User user){
	   // Fetch all successful order items for the user
	   List<OrderItem> orderItems = orderItemRepository.findSuccessfulOrderItemsByUserId(user.getUserId());
   
	   // Prepare the response map
    Map<String, Object> response = new HashMap<>();
      response.put("username", user.getUsername());
      response.put("role", user.getRole());
      
      // Transform orderItem into a list of product details
      List<Map<String, Object>> products = new ArrayList<>();
      for (OrderItem item : orderItems) {
          Product product = productRepository.findById(item.getProductId()).orElse(null);
          if (product == null) {
              continue; // Skip if the product does not exist
          }
          
          // Fetch the product image (if available)
          List<ProductImage> images = productImageRepository.findByProduct_ProductId(product.getProductId());
          String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
          
          // Create a product details map
          Map<String, Object> productDetails = new HashMap<>();
          productDetails.put("order_id", item.getOrder().getOrderId());
          productDetails.put("quantity", item.getQuantity());
          productDetails.put("total_price", item.getTotalPrice());
          productDetails.put("image_url", imageUrl);
          productDetails.put("product_id", product.getProductId());
          productDetails.put("name", product.getName());
          productDetails.put("description", product.getDescription());
          productDetails.put("price_per_unit", item.getPricePerUnit());

          products.add(productDetails);
      }
 // ADD the poducts list to the response
      
      response.put("products", products);
      return response;
   }
}
