package com.project.workboard.entity;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "task_card")
public class TaskCard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // refers to PK in task_card table
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	

	@Column(name = "is_active")
	private boolean isActive;
	
	@Column(name = "is_completed")
	private boolean isCompleted;
	
    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "created_by")
	private int createdBy;
    
    @Column(name = "updated_by")
	private int updatedBy;

    @Column(name = "updated_date", updatable = false, insertable = false)
    private LocalDateTime updatedDate;
    
    
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
    
    public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
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

	
	/*----------------@ManyToOne relationships----------------*/

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false) // refers to FK column in task_card table
	@JsonBackReference
    private BoardList boardList;

	public BoardList getBoardList() {
		return boardList;
	}

	public void setBoardList(BoardList boardList) {
		this.boardList = boardList;
	}
}
