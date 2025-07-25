package com.project.workboard.dto;

public class BoardListDTO {
	private int id;
	private int boardId;
	private String name;
	private int userId;

	/*
	 * private String email; // this is for demo purpose private String pwd; // this
	 * is for demo purpose
	 */

	public BoardListDTO() {
	}

	public BoardListDTO(int id, int boardId, String name, int userId) {
		super();
		this.id = id;
		this.boardId = boardId;
		this.userId = userId;
		this.name = name;
	}

	/*
	 * public BoardListDTO(int id, int boardId, String name, String email, String
	 * pwd) { super(); this.id = id; this.boardId = boardId; this.name = name;
	 * this.email = email; this.pwd = pwd; }
	 */

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
