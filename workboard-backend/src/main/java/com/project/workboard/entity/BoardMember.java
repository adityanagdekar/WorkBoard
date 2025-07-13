package com.project.workboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "board_member")
@IdClass(BoardMemberId.class)
public class BoardMember {
	@Id
	@ManyToOne
	@JoinColumn(name = "board_id")
	private Board board;
	
	@Id
	@ManyToOne
	@JoinColumn(name = "user_id")
	private AppUser user;
	
	@Column(name = "role")
    private int role;

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
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
}
