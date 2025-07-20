package com.project.workboard.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.AppUserDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {

//	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	@Autowired
	private AppUserRepository userRepository;

	private static final String SECRET = SecurityConstants.SECRET;
	private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	public String generateToken(Authentication authentication) {
		String email = authentication.getName();

		// get the logged-in user info
		Object principal = authentication.getPrincipal();
		
		int userId = -1;

		// check if principal is instanceOf AppUser
		if (principal instanceof AppUser) {
			// get the AppUser obj.
			AppUser user = (AppUser) principal;
			userId = user.getId();
		}

		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
		String tokenString = "";
		try {

			tokenString = Jwts.builder()
					.setSubject(email)
					.claim("userId", userId)
	                .claim("email", email)  
					.setIssuedAt(currentDate)
					.setExpiration(expireDate)
					.signWith(KEY, SignatureAlgorithm.HS512)
					.compact();

			System.out.println("New JWT : "+tokenString);
		} catch (Exception e) {
			System.out.println("Exception while generating JWT token: " + e.getMessage());
		}
		return tokenString;
	}

	public String getEmailFromJWT(String token) {
		Claims claims = getClaims(token);
		System.out.println("claims: " + claims.toString());
		String email = claims.getSubject();
		return email;
	}
	
	public int getUserIdFromJWT(String token) {
		Claims claims = getClaims(token);
		System.out.println("claims: " + claims.toString());
		int userId = (int) claims.get("userId");
		return userId;
	}
	
	public String getTokenFromRequest(HttpServletRequest request) {
		String jwtTokenString = ""; 
	    if (request.getCookies() != null) {
			// traversing thru all the Cookies to get the JWT token
	        for (Cookie cookie : request.getCookies()) {
	            if ("jwt".equals(cookie.getName())) {
	                jwtTokenString = cookie.getValue();
	                break;
	            }
	        }
	    }
		System.out.println("JWT token: "+jwtTokenString);
		return jwtTokenString;
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		Claims claims = getClaims(token);
		String email = claims.getSubject();
		return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	// Check if token is expired
	private boolean isTokenExpired(String token) {
		return getClaims(token).getExpiration().before(new Date());
	}

	// Get token claims
	private Claims getClaims(String token) {
		Claims claims;
		try {
			claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect",
					ex.fillInStackTrace());
		}
		return claims;
	}

	public Cookie getJWTCookie(Authentication authentication) {
		
		String tokenString = generateToken(authentication);

		Cookie jwtCookie = new Cookie("jwt", tokenString);
		jwtCookie.setHttpOnly(true);

		// isLocal = true -> which means we'r currently in dev env.: localhost
		boolean isLocal = true;
		jwtCookie.setSecure(!isLocal);

		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(5 * 60);
		
		return jwtCookie;
	}
}
