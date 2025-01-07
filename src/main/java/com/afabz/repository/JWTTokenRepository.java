package com.afabz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afabz.entity.JWTToken;

public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {

	Optional<JWTToken> findByToken(String token);

}
