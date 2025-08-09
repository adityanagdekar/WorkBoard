package com.project.workboard.dto;

import java.lang.reflect.Member;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.workboard.dto.SavedBoardDataDTO.MemberDataDTO;
import com.project.workboard.entity.Board;

public class BoardDataDTO {
	private String name, description;
	private Member[] members;
	private int userId;
	private int boardId;
	
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

	public BoardDataDTO(String name, String description, Member[] members, int userId, int boardId) {
		super();
		this.name = name;
		this.description = description;
		this.members = members;
		this.userId = userId;
		this.boardId = boardId;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
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

	@Override
	public String toString() {
		return "BoardDataDTO [ boardId=" + boardId + 
				" name=" + name + 
				", description=" + description + 
				", \n members=" + memberToString(members)
				+ ", userId=" + userId + " ]";
	}
	
	private String memberToString(Member[] members) {
		StringBuffer membersSbf = new StringBuffer();
		for(Member member: members) {
			membersSbf.append(" "+member.toString()+"\n");
		}
		return membersSbf.toString();
	}
}

/*
 * { "name": "Neo school LMS", 
 * "description": "Neo School Learning Management System", 
 * "members": [ 
 * 		{ "id": 1, "name": "John Doe", 
 * 			"isAdded": true, "role": 1 
 * 		} ], 
 * "userId": 1 }
 */