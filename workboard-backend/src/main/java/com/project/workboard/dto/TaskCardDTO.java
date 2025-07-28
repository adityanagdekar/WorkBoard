package com.project.workboard.dto;

public class TaskCardDTO {
	private int id;
	private String name;
	private String desc;
	private boolean isActive;
	private boolean isCompleted;
	
	public TaskCardDTO() {}
	
	public TaskCardDTO(int id, String name, String desc, boolean isActive, boolean isCompleted) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.isActive = isActive;
		this.isCompleted = isCompleted;
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
}
