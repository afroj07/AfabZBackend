package com.afabz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afabz.entity.Category;
import com.afabz.entity.Product;
import com.afabz.entity.ProductImage;
import com.afabz.repository.CategoryRepository;
import com.afabz.repository.ProductImageRepository;
import com.afabz.repository.ProductRepository;

@Service
public class ProductService {
 
@Autowired
private ProductRepository productRepository;

@Autowired
private ProductImageRepository productImagesRepository;

@Autowired
private CategoryRepository categoryRepository;

public List<Product>getProductByCategory(String categoryName){
	if(categoryName != null && !categoryName.isEmpty()) {
		Optional<Category>categoryOpt = categoryRepository.findByCategoryName(categoryName);
		if(categoryOpt.isPresent()) {
			Category category = categoryOpt.get();
			return productRepository.findByCategory_CategoryId(category.getCategoryId());
		} else {
			throw new RuntimeException("Category not found");
		}
	}
	 else {
			return productRepository.findAll();
		}
   }
	
	public List<String> getProductImages(Integer productId){
		List<ProductImage> productImages = productImagesRepository.findByProduct_ProductId(productId);
	    List<String> imageUrls = new ArrayList<>();
	   for(ProductImage image:productImages) {
		   imageUrls.add(image.getImageUrl());
	   }
	   return imageUrls;
	}
  }
