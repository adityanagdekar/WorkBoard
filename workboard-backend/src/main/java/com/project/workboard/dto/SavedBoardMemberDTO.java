package com.project.workboard.dto;

public class SavedBoardMemberDTO {
	private int userId;
	private int role;
	private String name;

	public SavedBoardMemberDTO() {
	}

	public SavedBoardMemberDTO(int userId, int role, String name) {
		super();
		this.userId = userId;
		this.role = role;
		this.name = name;
	}

	public String getUserName() {
		return name;
	}

	public void setUserName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "SavedBoardMemberDTO userId=" + userId + ", role=" + role + ", name=" + name
				+ "]";
	}

}
