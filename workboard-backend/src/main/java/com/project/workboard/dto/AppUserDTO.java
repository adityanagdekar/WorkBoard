package com.project.workboard.dto;

import com.project.workboard.entity.AppUser;

public class AppUserDTO {
	private Integer id;
	private String name;
	private String email;

	public AppUserDTO(AppUser appUser) {
		this.id = appUser.getId();
		this.name = appUser.getName();
		this.email = appUser.getEmail();
	}
	

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
