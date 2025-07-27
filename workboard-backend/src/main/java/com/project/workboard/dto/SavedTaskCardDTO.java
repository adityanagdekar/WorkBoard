package com.project.workboard.dto;

public class SavedTaskCardDTO {
	private int taskId;
	private TaskMemberData[] members;
	private String name;
	private String desc;
	
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
	
	public SavedTaskCardDTO(int taskId, TaskMemberData[] members, String name, String desc) {
		super();
		this.taskId = taskId;
		this.members = members;
		this.name = name;
		this.desc = desc;
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

	@Override
	public String toString() {
		return "SavedTaskCardDTO [taskId=" + taskId + 
				", members=" + memberToString(members) + ", name="
				+ name + ", desc=" + desc + "]";
	}
	
	private String memberToString(TaskMemberData[] members) {
		StringBuffer membersSbf = new StringBuffer();
		for(TaskMemberData member: members) {
			membersSbf.append(" "+member.toString()+"\n");
		}
		return membersSbf.toString();
	}
}
