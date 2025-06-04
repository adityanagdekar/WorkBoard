package com.project.workboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;

@RestController
@RequestMapping("/api/users")
public class AppUserController {
	@Autowired
	private AppUserRepository userRepo;

	@GetMapping
	public List<AppUser> getAllUsers() {
		return userRepo.findAll();
	}

	@PostMapping
    public AppUser createUser(@RequestBody AppUser user) {
		return null;  
	}
}
