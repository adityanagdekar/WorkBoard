package com.project.workboard.entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "board_list")
public class BoardList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // refers to PK in board_list table
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "created_by")
	private int createdBy;
    
    @Column(name = "updated_by")
	private int updatedBy;
    
    @Column(name = "updated_date", updatable = false, insertable = false)
    private LocalDateTime updatedDate;

    public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return createdBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	/*----------------@OneToMany relationships----------------*/

	// mappedBy refers to the field name in the child entity (TaskCard) 
	// that owns the relationship
    @OneToMany(mappedBy = "boardList", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TaskCard> taskCards = new ArrayList<>();
    
    
	/*----------------@ManyToOne relationships----------------*/
    
	public List<TaskCard> getTaskCards() {
		return taskCards;
	}

	public void setTaskCards(List<TaskCard> taskCards) {
		this.taskCards = taskCards;
	}


	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false) // refers to FK column in board_list table
    private Board board;

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

}
