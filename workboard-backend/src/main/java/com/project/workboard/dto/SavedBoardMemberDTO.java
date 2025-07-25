package com.project.workboard.dto;

public class SavedBoardMemberDTO {
	private int id;
	private int role;
	private String name;

	public SavedBoardMemberDTO() {
	}

	public SavedBoardMemberDTO(int id, int role, String name) {
		super();
		this.id = id;
		this.role = role;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "SavedBoardMemberDTO id=" + id + ", role=" + role + ", name=" + name
				+ "]";
	}

}
