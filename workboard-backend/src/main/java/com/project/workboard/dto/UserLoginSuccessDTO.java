package com.project.workboard.dto;

public class UserLoginSuccessDTO {
	private String msg;
	private AppUserDTO appUserData;
	
	public UserLoginSuccessDTO(String successMsg, AppUserDTO appUserData) {
		super();
		this.msg = successMsg;
		this.appUserData = appUserData;
	}

	public String getSuccessMsg() {
		return msg;
	}

	public void setSuccessMsg(String successMsg) {
		this.msg = successMsg;
	}

	public AppUserDTO getAppUserData() {
		return appUserData;
	}

	public void setAppUserData(AppUserDTO appUserData) {
		this.appUserData = appUserData;
	}

}
