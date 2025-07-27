package com.project.workboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDataDTO {
	private int id;
	private String name;
	private String description;
	private boolean isActive;
	private boolean isCompleted;
	private Member[] members;
	private int userId;
	private int listId;
	
	public TaskDataDTO() {}
	
	public TaskDataDTO(int id, String name, String description, 
			boolean isActive, boolean isCompleted, 
			Member[] members, int userId, int listId) {
		super();
		this.setId(id);
		this.name = name;
		this.description = description;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
		this.members = members;
		this.userId = userId;
		this.listId = listId;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Member[] getMembers() {
		return members;
	}

	public void setMembers(Member[] members) {
		this.members = members;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
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
    "userId": 1,
    "listId": 6
}
 * */
