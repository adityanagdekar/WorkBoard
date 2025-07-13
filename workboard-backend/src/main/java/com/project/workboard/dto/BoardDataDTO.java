package com.project.workboard.dto;

import java.lang.reflect.Member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.workboard.entity.Board;

public class BoardDataDTO {
	private String name, description;
	private Member[] members;
	private int userId;

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

	public BoardDataDTO(String name, String description, Member[] members, int userId) {
		super();
		this.name = name;
		this.description = description;
		this.members = members;
		this.userId = userId;
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
}

/*
 * { "name": "Neo school LMS", "description":
 * ": Neo School Learning Management System", "members": [ { "id": 1, "name":
 * "John Doe", "added": true, "role": 1 } ], "userId": 1 }
 */