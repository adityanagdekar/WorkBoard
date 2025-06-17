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
	public ResponseEntity<JwtResponseDTO> loginUser(@RequestBody LoginRequestDTO loginReq) {
		
		System.out.println("login req: "+loginReq.toString());
		
		Authentication authentication = authenticationManager
				.authenticate(
						new UsernamePasswordAuthenticationToken(loginReq.getEmail(), 
								loginReq.getPassword()
								));
		String tokenString = jwtService.generateToken(authentication);
		JwtResponseDTO jwtResponse = new JwtResponseDTO(tokenString);
		return ResponseEntity.ok(jwtResponse);
	}
}
