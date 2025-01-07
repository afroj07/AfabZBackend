package com.afabz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afabz.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Optional<Category> findByCategoryName(String categoryName);

}
