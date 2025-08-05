package com.project.workboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "task_member")
@IdClass(TaskMemberId.class)
public class TaskMember {
	@Id
	@ManyToOne
	@JoinColumn(name = "card_id")
	private TaskCard taskCard;

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id")
	private AppUser user;

	@Column(name = "role")
	private int role;

	public TaskCard getTaskCard() {
		return taskCard;
	}

	public void setTaskCard(TaskCard taskCard) {
		this.taskCard = taskCard;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "TaskMember [ taskCard=" + taskCard.getId() + ", user=" + user.getId() + ", role=" + role + "]";
	}

}
