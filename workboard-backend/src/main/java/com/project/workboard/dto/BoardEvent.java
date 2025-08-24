package com.project.workboard.dto;

public class BoardEvent {
	public enum Type {
		UPSERT, DELETE, LIST_ADDED, LIST_NAME_UPDATED, TASK_ADDED, TASK_UPDATED
	}

	private Type type;
	private int boardId;
	private Integer userId;
	// BoardDTO for UPSERT, or null for DELETE
	private Object payload;

	// NOT-YET IMPLEMENTED: updatedAt epoch millis or revision
	private long version;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
