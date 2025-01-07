package com.afabz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afabz.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	List<ProductImage> findByProduct_ProductId(Integer productId);

}
