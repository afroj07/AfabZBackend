package com.afabz.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import com.afabz.entity.User;
import com.afabz.repository.UserRepository;
import com.afabz.service.AuthService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebFilter(urlPatterns = "/api/*") //Apply the filter to all /api/* endpoints

public class AuthenticationFilter implements Filter{

	private final AuthService authService;
	private final UserRepository userRepository;
	
	//constructor to inject AuthService and UserRepository
	public AuthenticationFilter(AuthService authService, UserRepository userRepository) {
		System.out.println("Authentication initialized");
		this.authService = authService;
		this.userRepository = userRepository;
	}
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("EXECUTING FILTER");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		//Get the request uri
		String requestURI = httpRequest.getRequestURI();
		System.out.println("Request URI:" + requestURI);
		
		//Allow  access to specific endpoints
		if(requestURI.equals("/api/users/register") || requestURI.equals("/api/auth/login")) {
			System.out.println("Request allowed without authentication:" + requestURI);
			chain.doFilter(request, response);
		    return;
		}
		
		//Handle preflight (OPTIONS) requests
		if(httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
			httpResponse.setHeader("Access-Control-Allow-Origin","http://localhost:5173");
			httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		    httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
		    httpResponse.setStatus(HttpServletResponse.SC_OK);
		    return;
		}
		
		// Existing token validation logic
		String token = getAuthTokenFromCookies(httpRequest);
		System.out.println("Extracted Token: " + token);
		
		if(token == null || !authService.validateToken(token)) {
			System.out.println("Token validation failed or token is missing.");
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			httpResponse.getWriter().write("Unauthorized: Invalid or missing token");
		  return;
		}
		
		//Extract user information and proceed
		String username = authService.extractUsername(token);
		System.out.println("Extracted Username:" + username);
		
		Optional<User> userOptional = userRepository.findByUsername(username);
		
		if(userOptional.isEmpty()) {
			System.out.println("User not found in database for username:" + username);
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			httpResponse.getWriter().write("Unauthorized: User not found");
			return;
		}
		
		// Log authenticated user details
		User authenticatedUser = userOptional.get();
		System.err.println("Authenticated User:" + authenticatedUser.getUsername() + ", Role:" + authenticatedUser.getRole());
	
	// Attach authenticated user details to the request
		httpRequest.setAttribute("authenticatedUser", authenticatedUser);
		chain.doFilter(request, response);
	}

	// Utility method to extract authtoken from cookies
	private String getAuthTokenFromCookies(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        Arrays.stream(cookies).forEach(cookie -> 
	            System.out.println("Cookie Name: " + cookie.getName() + ", Value: " + cookie.getValue())
	        );
	        return Arrays.stream(cookies)
	                     .filter(cookie -> "authToken".equals(cookie.getName()))
	                     .map(Cookie::getValue)
	                     .findFirst()
	                     .orElse(null);
	    }
	    System.out.println("No cookies found in the request");
	    return null;
	}

	
}
