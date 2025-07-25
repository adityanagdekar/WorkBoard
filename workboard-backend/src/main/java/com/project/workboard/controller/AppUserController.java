package com.project.workboard.controller;

import java.util.List;
import java.util.Map;

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

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.AppUserDTO;
import com.project.workboard.dto.JwtResponseDTO;
import com.project.workboard.dto.LoginRequestDTO;
import com.project.workboard.dto.RegisterRequestDTO;
import com.project.workboard.dto.UserLoginSuccessDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.security.JwtService;
import com.project.workboard.service.AppUserService;
import com.project.workboard.service.CustomUserDetailsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class AppUserController {
	@Autowired
	private AppUserService appUserService;

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		return appUserService.getAllUsers();
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerReq) {
		System.out.println("register req: " + registerReq.toString());
		return appUserService.registerUser(registerReq);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginReq, 
			HttpServletResponse response) {
		System.out.println("login req: " + loginReq.toString());
		return appUserService.loginUser(loginReq, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		return appUserService.logout(response);
	}

	@GetMapping("/session")
	public ResponseEntity<?> checkSession(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Inside AppUserController: checkSession");
		return appUserService.checkSession(request, response);
	}
}
