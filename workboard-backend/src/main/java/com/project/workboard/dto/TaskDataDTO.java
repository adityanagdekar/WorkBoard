package com.project.workboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDataDTO {
	private String name;
	private String description;
	private boolean isActive;
	private boolean isCompleted;
	private Member[] members;
	
	public TaskDataDTO() {}
	
	public TaskDataDTO(String name, String description, boolean isActive, boolean isCompleted, Member[] members) {
		super();
		this.name = name;
		this.description = description;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
		this.members = members;
	}

	public static class Member {
		private int id;
		private boolean isAdded;
		private int role;

		public Member() {
		}

		public Member(int id, int role, boolean isAdded) {
			super();
			this.id = id;
			this.role = role;
			this.isAdded = isAdded;
		}

		public int getId() {
			return id;
		}
		
		@JsonProperty("isAdded")
		public boolean isAdded() {
			return isAdded;
		}
		
		@JsonProperty("isAdded")
		public void setAdded(boolean isAdded) {
			this.isAdded = isAdded;
		}
		
		public int getRole() {
			return role;
		}

		public void setRole(int role) {
			this.role = role;
		}


		@Override
		public String toString() {
			return "Member [id=" + id + ", isAdded=" + isAdded + ", role=" + role + "]";
		}

	}
}

/*
 * Obj. for reference:
 * 
 * {
    "name": "Build Dashboard",
    "description": "Dashboard must show list of all anticipated leads and regular customers",
    "is_active": true,
    "is_completed": false,
    "members": [
        {
            "id": 1,
            "name": "John Doe",
            "isAdded": true,
            "role": 0
        },
        {
            "id": 8,
            "name": "zack jones",
            "isAdded": true,
            "role": 0
        },
        {
            "id": 10,
            "name": "Shree",
            "isAdded": true,
            "role": 1
        }
    ]
}
 * */
