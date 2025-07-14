package com.project.workboard.dto;

public class SavedBoardDataDTO {
	private int boardId;
	private int[] memberIds;
	
	public SavedBoardDataDTO() {}
	
	public SavedBoardDataDTO(int boardId, int[] memberIds) {
		super();
		this.boardId = boardId;
		this.memberIds = memberIds;
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
	
}
