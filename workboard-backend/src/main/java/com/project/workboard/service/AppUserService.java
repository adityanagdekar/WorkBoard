package com.project.workboard.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.AppUserDTO;
import com.project.workboard.dto.LoginRequestDTO;
import com.project.workboard.dto.RegisterRequestDTO;
import com.project.workboard.dto.UserLoginSuccessDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.security.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AppUserService {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwtService;

	public ResponseEntity<?> getAllUsers() {

		try {
			List<AppUserDTO> users = appUserRepository.findAll().stream().map(AppUserDTO::new).toList();

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = true;

			String responseMsg = (users.size() > 0) ? "Users fetched successfully" : "There are no users present";

			ApiResponseDTO<List<AppUserDTO>> apiResponse = new ApiResponseDTO<List<AppUserDTO>>(successFlag, users,
					responseMsg);

			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			System.out.println("Exception while fetching all users: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while fetching all users");
		}
	}

	public ResponseEntity<?> registerUser(RegisterRequestDTO registerReq) {
		try {

			if (appUserRepository.findByEmail(registerReq.getEmail()).isPresent()) {
				return ResponseEntity.badRequest().build(); // email already used
			}

			AppUser user = new AppUser();
			user.setName(registerReq.getName());
			user.setEmail(registerReq.getEmail());
			user.setPwd(passwordEncoder.encode(registerReq.getPassword()));

			AppUser savedUser = appUserRepository.save(user);

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = (savedUser.getId() > 0);

			String responseMsg = (savedUser.getId() > 0) ? "User saved successfully" : "Error while saving user";

			ApiResponseDTO<Integer> apiResponse = new ApiResponseDTO<Integer>(successFlag, savedUser.getId(),
					responseMsg);

			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			System.out.println("Exception while registering the user: " + registerReq.toString() + " \n Exception: "
					+ e.getMessage());

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while registering the user");
		}
	}

	public ResponseEntity<?> loginUser(LoginRequestDTO loginReq, HttpServletResponse response) {

		try {
			String email = loginReq.getEmail();
			String pwd = loginReq.getPassword();

			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, pwd));

			String tokenString = jwtService.generateTokenBasedOnAuth(authentication);

			Cookie jwtCookie = jwtService.getJWTCookie(tokenString);

			response.addCookie(jwtCookie);

			UserLoginSuccessDTO loginSuccessObj = new UserLoginSuccessDTO("Logged in successfully", null);

			// get the logged-in user info
			Object principal = authentication.getPrincipal();

			// check if principal is instanceOf AppUser
			if (principal instanceof AppUser) {

				// get the AppUser obj.
				AppUser user = (AppUser) principal;

				// Get logged-in user's info using AppUserDTO
				AppUserDTO appUserData = new AppUserDTO(user);

				loginSuccessObj.setAppUserData(appUserData);
			}

			return ResponseEntity.ok(loginSuccessObj);

		} catch (Exception e) {
			System.out.println(
					"Exception while logging-in the user: " + loginReq.toString() + " \n Exception: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while logging-in the user");
		}

	}

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

	public ResponseEntity<?> checkSession(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Inside AppUserService: checkSession");
		try {
			String jwtToken = null;
			if (request.getCookies() != null) {
				for (Cookie cookie : request.getCookies()) {

					if ("jwt".equals(cookie.getName())) {
						jwtToken = cookie.getValue();
					}
				}
			}

			if (jwtToken == null || jwtToken.isEmpty()) {
				System.out.println("JWT is empty & expried");
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body("JWT is empty & expried");
			} else {
				boolean tokenFlag = jwtService.shouldRefreshToken(jwtToken);
				System.out.println("is JWT token expired: " + tokenFlag);

				if (tokenFlag) {
					// get the new token
					String newJwtToken = jwtService.refreshToken(jwtToken);
					// get the new cookie
					Cookie newJwtCookie = jwtService.getJWTCookie(newJwtToken);
					response.addCookie(newJwtCookie);
					System.out.println("Token refresh, new jwt token: " + newJwtToken);
					// update the jwt token
					jwtToken = newJwtToken;
				}
			}

			// To Extract email
			String email = jwtService.getEmailFromJWT(jwtToken);
			return ResponseEntity.ok(Map.of("email", email));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired JWT");
		}
	}

}
