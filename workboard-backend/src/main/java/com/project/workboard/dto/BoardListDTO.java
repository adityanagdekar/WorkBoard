package com.project.workboard.dto;

public class BoardListDTO {
	private int id;
	private int boardId;
	private String name;

	public BoardListDTO() {
	}

	public BoardListDTO(int id, int boardId, String name) {
		super();
		this.id = id;
		this.boardId = boardId;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
