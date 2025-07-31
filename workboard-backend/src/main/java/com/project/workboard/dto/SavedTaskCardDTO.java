package com.project.workboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SavedTaskCardDTO {
	private int taskId;
	private TaskMemberData[] members;
	private String name;
	private String desc;
	@JsonProperty("isActive")	
	private boolean isActive;
	@JsonProperty("isCompleted")	
	private boolean isCompleted;
	
	public static class TaskMemberData {
		private int memberId;
		private int memberRole;
		
		public TaskMemberData() {}

		public TaskMemberData(int memberId, int memberRole) {
			super();
			this.memberId = memberId;
			this.memberRole = memberRole;
		}

		public int getMemberId() {
			return memberId;
		}

		public void setMemberId(int memberId) {
			this.memberId = memberId;
		}

		public int getMemberRole() {
			return memberRole;
		}

		public void setMemberRole(int memberRole) {
			this.memberRole = memberRole;
		}

		@Override
		public String toString() {
			return "MemberIdRole [memberId=" + memberId + ", memberRole=" + memberRole + "]";
		}
		
	}
	
	public SavedTaskCardDTO() {}
	
	public SavedTaskCardDTO(int taskId, TaskMemberData[] members, String name, 
			String desc, boolean isActive, boolean isCompleted) {
		super();
		this.taskId = taskId;
		this.members = members;
		this.name = name;
		this.desc = desc;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public TaskMemberData[] getMembers() {
		return members;
	}

	public void setMembers(TaskMemberData[] members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	@Override
	public String toString() {
		return "SavedTaskCardDTO [ taskId=" + taskId + 
				", members=" + memberToString(members) + ", name="
				+ name + ", desc=" + desc 
				+" isActive: "+isActive+" isCompleted: "+isCompleted+" ]";
	}
	
	private String memberToString(TaskMemberData[] members) {
		StringBuffer membersSbf = new StringBuffer();
		for(TaskMemberData member: members) {
			membersSbf.append(" "+member.toString()+"\n");
		}
		return membersSbf.toString();
	}
}
