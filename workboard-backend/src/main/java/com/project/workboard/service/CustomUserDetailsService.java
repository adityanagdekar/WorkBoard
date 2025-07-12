package com.project.workboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import com.project.workboard.entity.AppUser;
import com.project.workboard.repository.AppUserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private AppUserRepository appUserRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		// return new User(appUser.getEmail(), appUser.getPwd(), new ArrayList<>());
		return appUser;
	}

}
