package com.project.workboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.JwtResponseDTO;
import com.project.workboard.dto.LoginRequestDTO;
import com.project.workboard.dto.RegisterRequestDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.security.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class AppUserController {

	@Autowired
	private AppUserRepository userRepo;
	
	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/all")
	public ResponseEntity<String> getAllUsers() {
		return new ResponseEntity<>("Hey Hi", HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<AppUser> registerUser(@RequestBody RegisterRequestDTO registerReq) {
		
		System.out.println("register req: "+registerReq.toString());
		
		if (userRepo.findByEmail(registerReq.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().build(); // email already used
		}

		AppUser user = new AppUser();
		user.setName(registerReq.getName());
		user.setEmail(registerReq.getEmail());
		user.setPwd(passwordEncoder.encode(registerReq.getPassword()));

		AppUser saved = userRepo.save(user);
		return ResponseEntity.ok(saved);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginReq,  
			HttpServletResponse response) {
		
		System.out.println("login req: "+loginReq.toString());
		
		Authentication authentication = authenticationManager
				.authenticate(
						new UsernamePasswordAuthenticationToken(loginReq.getEmail(), 
								loginReq.getPassword()
								));
		String tokenString = jwtService.generateToken(authentication);
		System.out.println("tokenString in AppUserController: "+tokenString);
		
		Cookie jwtCookie = new Cookie("jwt", tokenString);
		jwtCookie.setHttpOnly(true);
		
		// isLocal = true -> which means we'r currently in dev env.: localhost
		boolean isLocal = true;
		jwtCookie.setSecure(!isLocal);
		
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(10*60);
		response.addCookie(jwtCookie);
		
//		JwtResponseDTO jwtResponse = new JwtResponseDTO(tokenString);
		return ResponseEntity.ok("Logged in successfully");
	}
	
	@PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("jwt", null);
	    cookie.setHttpOnly(true);
	    cookie.setSecure(false);
	    cookie.setPath("/");
	    // to delete the cookie immediately
	    cookie.setMaxAge(0); 
	    response.addCookie(cookie);

	    return ResponseEntity.ok("Logged out successfully");
	}
}
