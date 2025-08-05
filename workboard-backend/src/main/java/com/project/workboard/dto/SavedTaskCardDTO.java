package com.project.workboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SavedTaskCardDTO {
	private int id;
	private String name;
	private String description;	
	private boolean isActive;
	private boolean isCompleted;
	private SavedTaskMemberDTO[] members;
//	private TaskMemberData[] members;
	
	public SavedTaskCardDTO() {}
	
	public SavedTaskCardDTO (int taskId, String name, 
			String desc, boolean isActive, boolean isCompleted) {
		super();
		this.id = taskId;
		this.name = name;
		this.description = desc;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
	}
	
	public SavedTaskCardDTO(int taskId, String name, 
			String desc, boolean isActive, 
			boolean isCompleted, SavedTaskMemberDTO[] members) {
		super();
		this.id = taskId;
		this.name = name;
		this.description = desc;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
		this.members = members;
	}

	public int getId() {
		return id;
	}

	public void setId(int taskId) {
		this.id = taskId;
	}

	public SavedTaskMemberDTO[] getMembers() {
		return members;
	}

	public void setMembers(SavedTaskMemberDTO[] members) {
		this.members = members;
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

	public void setDescription(String desc) {
		this.description = desc;
	}

	@JsonProperty("isActive")
	public boolean isActive() {
		return isActive;
	}

	@JsonProperty("isActive")
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@JsonProperty("isCompleted")	
	public boolean isCompleted() {
		return isCompleted;
	}

	@JsonProperty("isCompleted")	
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	@Override
	public String toString() {
		return "task-card -> [ taskId=" + id + 
				", members=" + (members.length > 0 ? memberToString(members) : "[]") + ", name="
				+ name + ", desc=" + description 
				+" isActive: "+isActive+" isCompleted: "+isCompleted+" ]";
	}
	
	private String memberToString(SavedTaskMemberDTO[] members) {
		StringBuffer membersSbf = new StringBuffer();
		for(SavedTaskMemberDTO member: members) {
			membersSbf.append(" "+member.toString()+"\n");
		}
		return membersSbf.toString();
	}
}

/*public static class TaskMemberData {
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

}*/
