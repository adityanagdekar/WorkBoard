package com.project.workboard.dto;

public class SavedBoardDataDTO {
	private int boardId;
	private int[] memberIds;
	private String boardName;
	private String boardDesc;
	
	public SavedBoardDataDTO() {}
	
	public SavedBoardDataDTO(int boardId, int[] memberIds, String boardName, String boardDesc) {
		super();
		this.boardId = boardId;
		this.memberIds = memberIds;
		this.boardName = boardName;
		this.boardDesc = boardDesc;
	}
	
	public int getBoardId() {
		return boardId;
	}
	
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	
	public int[] getMemberIds() {
		return memberIds;
	}
	
	public void setMemberIds(int[] memberIds) {
		this.memberIds = memberIds;
	}
	
	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	
	public String getBoardDesc() {
		return boardDesc;
	}

	public void setBoardDesc(String boardDesc) {
		this.boardDesc = boardDesc;
	}
	
}
