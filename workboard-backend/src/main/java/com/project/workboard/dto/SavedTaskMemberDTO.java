package com.project.workboard.dto;

public class SavedTaskMemberDTO {
	private int userId;
	private int role;
	
	public SavedTaskMemberDTO() {}

	public SavedTaskMemberDTO(int userId, int role) {
		super();
		this.userId = userId;
		this.role = role;
	}

	@Override
	public String toString() {
		return "SavedTaskMemberDTO [userId=" + userId + ", role=" + role + "]";
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}
