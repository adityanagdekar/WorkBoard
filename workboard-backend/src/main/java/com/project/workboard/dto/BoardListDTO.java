package com.project.workboard.dto;

import java.util.Arrays;

public class BoardListDTO {
	private int id;
	private int boardId;
	private String name;
	private int userId;
	private SavedTaskCardDTO[] cards = new SavedTaskCardDTO[0];

	public BoardListDTO() {
	}
	
	public BoardListDTO(int id, int boardId, String name, int userId) {
		super();
		this.id = id;
		this.boardId = boardId;
		this.userId = userId;
		this.name = name;
	}

	public BoardListDTO(int id, int boardId, String name, int userId, SavedTaskCardDTO[] cards) {
		super();
		this.id = id;
		this.boardId = boardId;
		this.userId = userId;
		this.name = name;
		this.cards = cards;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SavedTaskCardDTO[] getCards() {
		return cards;
	}

	public void setCards(SavedTaskCardDTO[] cards) {
		this.cards = cards;
	}
	
	@Override
	public String toString() {
		return "BoardList-> [ id=" + id + ", boardId=" + boardId + 
				", name=" + name + ", userId=" + userId + 
				", cards=" + cardsToString(cards) + " ]";
	}

	private String cardsToString(SavedTaskCardDTO[] taskCardDTOs) {
		StringBuffer buffer= new StringBuffer();
		for(SavedTaskCardDTO savedTaskCardDTO: taskCardDTOs) {
			buffer.append("\n "+savedTaskCardDTO.toString());
		}
		return buffer.toString();
	}

	/*
	 * public String getEmail() { return email; }
	 * 
	 * public void setEmail(String email) { this.email = email; }
	 * 
	 * public String getPwd() { return pwd; }
	 * 
	 * public void setPwd(String pwd) { this.pwd = pwd; }
	 */

}
