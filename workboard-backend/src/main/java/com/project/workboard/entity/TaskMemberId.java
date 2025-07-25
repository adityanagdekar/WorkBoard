package com.project.workboard.entity;

import java.io.Serializable;
import java.util.Objects;

public class TaskMemberId implements Serializable {

	private Integer taskCard;
	private Integer user;
	
	public TaskMemberId() {}

	public TaskMemberId(Integer taskCard, Integer user) {
		super();
		this.taskCard = taskCard;
		this.user = user;
	}

	public Integer getTaskCard() {
		return taskCard;
	}

	public void setTaskCard(Integer taskCard) {
		this.taskCard = taskCard;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) 
        	return true;
        if (!(obj instanceof TaskMemberId)) 
        	return false;
        
        TaskMemberId otherTaskMemberId = (TaskMemberId) obj;
        
        return Objects.equals(taskCard, otherTaskMemberId.taskCard) &&
               Objects.equals(user, otherTaskMemberId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskCard, user);
    }
}
