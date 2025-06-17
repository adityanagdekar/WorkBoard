package com.project.workboard.security;

import java.security.Key;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

		String token = Jwts.builder()
				.setSubject(email)
				.setIssuedAt(currentDate)
				.setExpiration(expireDate)
				.signWith(KEY, SignatureAlgorithm.HS512)
				.compact();

		System.out.println("New JWT :");
		System.out.println(token);
		return token;
	}

	public String getEmailFromJWT(String token) {
		Claims claims = getClaims(token);
		String email = claims.getSubject();
		return email;
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
        try{
        	claims = Jwts
        			.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was exprired or incorrect",
					ex.fillInStackTrace());
		}
        return claims;
    }
}
