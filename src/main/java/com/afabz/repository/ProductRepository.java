package com.afabz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afabz.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  List<Product> findByCategory_CategoryId(Integer categoryId);
}
