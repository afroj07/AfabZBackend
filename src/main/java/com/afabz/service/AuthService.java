package com.afabz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.afabz.entity.JWTToken;
import com.afabz.entity.User;
import com.afabz.repository.JWTTokenRepository;
import com.afabz.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

  // Securely generated signing key
  private final Key  SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  private final UserRepository userRepository;
  private final JWTTokenRepository jwtTokenRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  
  @Autowired
  public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository) {
	  this.userRepository = userRepository;
	  this.jwtTokenRepository = jwtTokenRepository;
	  this.passwordEncoder = new BCryptPasswordEncoder();
  }
  
  public User authenticate(String username, String password) {
	  // Find user by username
	  
	  User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("Invalid username or password"));
	  
	  // Verifhy password
	  
	  if(!passwordEncoder.matches(password, user.getPassword())) {
		  throw new RuntimeException("Invalid username or password");
	  }
	  return user;
  }
  
  public String generateToken(User user) {
	  // Generate a secure key for HS512 algorithm
     
	  String token  = Jwts.builder()
			   .setSubject(user.getUsername())
			   .claim("role",user.getRole().name())
			   .setIssuedAt(new Date())
			   .setExpiration(new Date(System.currentTimeMillis()+3600000)) //1h
			   .signWith(SIGNING_KEY,SignatureAlgorithm.HS512)
			   .compact();
	  
	  saveToken(user, token);
	  return token;
  }

public void saveToken(User user, String token) {
	JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now().plusHours(1));
	jwtTokenRepository.save(jwtToken);
}

public void logout(HttpServletResponse response) {
	Cookie cookie = new Cookie("authToken", null);
	cookie.setHttpOnly(true);
	cookie.setMaxAge(0);
	cookie.setPath("/");
	response.addCookie(cookie);
	
	// Invalid the token in the database(if exists)
	Optional<JWTToken>jwtToken = jwtTokenRepository.findByToken(cookie.getValue());
	jwtToken.ifPresent(jwtTokenRepository::delete);
}

public boolean validateToken(String token) {
	try {
		//parse and validate the token
		Jwts.parserBuilder()
		.setSigningKey(SIGNING_KEY)
		.build()
		.parseClaimsJws(token);
		
		//Check if the token existx in the database and is not expired
		Optional<JWTToken>jwtToken = jwtTokenRepository.findByToken(token);
		return jwtToken.isPresent() && jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
	}catch(Exception e) {
		return false;
	}
	
}
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}


