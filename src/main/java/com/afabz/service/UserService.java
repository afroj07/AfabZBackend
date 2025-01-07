package com.afabz.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.afabz.entity.User;
import com.afabz.repository.UserRepository;

@Service
public class UserService {
    

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
    @Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	public User registerUser(User user) {
	 
		// Check if user name or email already register
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("Username is already taken");
		}
		
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already registered");
		}
		
		// Encode password before
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	
		// Save The user data
		return userRepository.save(user);
	}
}
