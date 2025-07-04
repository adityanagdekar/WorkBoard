package com.project.workboard.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.workboard.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("JWT Filter triggered for URI: " + request.getRequestURI());
		
		
		/*
		    // Read JWT from Authorization header
			String authHeader = request.getHeader("Authorization");
	
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}
			String token = authHeader.substring(7);
		*/

		String jwtTokenString = ""; 
	    if (request.getCookies() != null) {
			// traversing thru all the Cookies to get the JWT token
	        for (Cookie cookie : request.getCookies()) {
	            if (cookie.getName().equals("jwt")) {
	                jwtTokenString = cookie.getValue();
	                break;
	            }
	        }
	    }
		System.out.println("jwt token: "+jwtTokenString);
		
		if (jwtTokenString.length()==0){
			// JWT token has not yet been assigned
			filterChain.doFilter(request, response);
			return;
		}
		
		String email = jwtService.getEmailFromJWT(jwtTokenString);

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);

			if (jwtService.validateToken(jwtTokenString, userDetails)) {
				UsernamePasswordAuthenticationToken authenticationToken = new 
						UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());

				authenticationToken.setDetails(new WebAuthenticationDetailsSource()
						.buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}

		filterChain.doFilter(request, response);
	}

}
