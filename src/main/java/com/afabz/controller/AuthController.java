package com.afabz.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.afabz.dto.LoginRequest;
import com.afabz.entity.User;
import com.afabz.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials="true")
@RequestMapping("/api/auth")
public class AuthController {
   
    	
    	private final AuthService authService;
    	
    	public AuthController(AuthService authService) {
    		this.authService = authService;
    	}
        
    	@PostMapping("/login")
    	@CrossOrigin(origins="http://localhost:5173")
    	public ResponseEntity<?>login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    		try {
    			// Authentication user and get the role
    			User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
    			
    			// Generate JWT token
    			String token = authService.generateToken(user);
    			
    			//Set token as HttpOnly cookie
    			Cookie cookie = new Cookie("authToken", token);
    			cookie.setHttpOnly(true);
    			cookie.setSecure(false); //Set true in production with Https
    			cookie.setPath("/");
    			cookie.setMaxAge(3600); // 1hour
    			cookie.setDomain("localhost");
    			response.addCookie(cookie);
    			
    			response.addHeader("Set-Cookie", String.format("authToken=%s; HttpOnly; Path=/; Max-Age=3600; SameSite=None", token));
    			
    			//Return user role in response body
    			
    			Map<String, Object> responseBody = new HashMap<>();
    			responseBody.put("message", "Login successful");
    			responseBody.put("role", user.getRole().name());
    			responseBody.put("username", user.getUsername());
    			return ResponseEntity.ok(responseBody);
    		}catch(RuntimeException e){
    	       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    		}
    	}
    	
    	@PostMapping("/logout")
    	public ResponseEntity<Map<String,String>> logout(HttpServletResponse response) {
    		try {
    			authService.logout(response);
    			Map<String,String> responseBody = new HashMap<>();
    			responseBody.put("message", "Logout successful");
    			return ResponseEntity.ok(responseBody);
    		}catch(RuntimeException e) {
    			Map<String, String> errorResponse = new HashMap<>();
    			errorResponse.put("message", "Logout failed");
    			return ResponseEntity.status(500).body(errorResponse);
    		}
    	}
}
