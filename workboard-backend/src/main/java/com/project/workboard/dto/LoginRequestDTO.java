package com.project.workboard.dto;

public class LoginRequestDTO {
	private String email;
	private String password;

	@Override
	public String toString() {
		return "LoginRequestDTO [email=" + email + ", password=" + password + "]";
	}

	// Getters and Setters
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
}
