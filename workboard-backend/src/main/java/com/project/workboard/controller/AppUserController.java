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
import com.project.workboard.service.CustomUserDetailsService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

	@GetMapping("/users")
	public ResponseEntity<List<AppUserDTO>> getAllUsers() {
		List<AppUserDTO> users = userRepo.findAll()
				.stream()
				.map(AppUserDTO::new).toList();
	    return ResponseEntity.ok(users);
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerReq) {
		
		System.out.println("register req: "+registerReq.toString());
		
		if (userRepo.findByEmail(registerReq.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().build(); // email already used
		}

		AppUser user = new AppUser();
		user.setName(registerReq.getName());
		user.setEmail(registerReq.getEmail());
		user.setPwd(passwordEncoder.encode(registerReq.getPassword()));

		AppUser savedUser = userRepo.save(user);
		ApiResponseDTO<Integer> apiResponse = new 
				ApiResponseDTO<Integer>(true, 
						savedUser.getId(), 
						"User registered successfully!!");
		return ResponseEntity.ok(apiResponse);
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
		System.out.println("token in AppUserController: "+tokenString);
		
		Cookie jwtCookie = new Cookie("jwt", tokenString);
		jwtCookie.setHttpOnly(true);
		
		// isLocal = true -> which means we'r currently in dev env.: localhost
		boolean isLocal = true;
		jwtCookie.setSecure(!isLocal);
		
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(5*60);
		response.addCookie(jwtCookie);

		UserLoginSuccessDTO loginSuccessObj = new 
				UserLoginSuccessDTO("Logged in successfully" , null);
		
		// get the logged-in user info
		Object principal =  authentication.getPrincipal();
		
		// check if principal is instanceOf AppUser
		if (principal instanceof AppUser) {
			
			// get the AppUser obj.
		    AppUser user = (AppUser) principal;
		    
		    // Get logged-in user's info using AppUserDTO
			AppUserDTO appUserData = new AppUserDTO(user);
			
			loginSuccessObj.setAppUserData(appUserData);
		}
		
		return ResponseEntity.ok(loginSuccessObj);
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
	
	@GetMapping("/session")
	public ResponseEntity<?> checkSession(HttpServletRequest request){
		String jwtString=null;
		if (request.getCookies() != null) {
			for(Cookie cookie: request.getCookies()) {
				
				if ( "jwt".equals(cookie.getName()) ) {
					jwtString = cookie.getValue();
				}
			}
		}
		
		if (jwtString == null || jwtString.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT not found");
		}
		
		try {
			// To Extract email
	        String email = jwtService.getEmailFromJWT(jwtString); 
	        return ResponseEntity.ok(Map.of("email", email));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT");
	    }
		
	}
}
