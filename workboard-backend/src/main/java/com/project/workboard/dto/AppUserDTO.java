package com.project.workboard.dto;

import com.project.workboard.entity.AppUser;

public class AppUserDTO {
	private Integer id;
	private String name;
	private String email;
	
	public AppUserDTO() {}

	public AppUserDTO(AppUser user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
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
