package com.project.workboard.entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "board")
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // refers to PK column in board table
    private Integer id;
	
    @Column(name = "name") 
    private String name;

	@Column(name = "board_desc")
    private String description;

    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;
    

	/*----------------@ManyToOne relationships----------------*/
    
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // refers to FK column in board table
    private AppUser user;

	public Integer getId() {
		return id;
	}
    
    public void setUser(AppUser user) {
		this.user = user;
	}

	public Integer getUser() {
		return (user != null ? user.getId() : -1);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	@Override
	public String toString() {
		return "Board [board_id=" + id + ", user_id=" + (user != null ? user.getId() : -1) + 
				", boardName=" + name + " createdDate="+createdDate+"]";
	}
}
