package com.afabz.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afabz.entity.CartItem;
import com.afabz.entity.Product;
import com.afabz.entity.User;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {
  Optional<CartItem> findByUserAndProduct(User user, Product product);
 
   @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user = :user")
   int countTotalItems(@Param("user") User user);
   
   List<CartItem> findAllByUser(User user);
}
