package com.project.workboard.entity;

import java.io.Serializable;
import java.util.Objects;

public class BoardMemberId implements Serializable {
	private Integer board;
	private Integer user;
	
	public BoardMemberId() {}
	
	public BoardMemberId(Integer boardId, Integer userId) {
		this.board = boardId;
		this.user = userId;
	}
	
	public Integer getBoardId() {
		return board;
	}

	public void setBoardId(Integer boardId) {
		this.board = boardId;
	}

	public Integer getUserId() {
		return user;
	}

	public void setUserId(Integer userId) {
		this.user = userId;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj) 
        	return true;
        if (!(obj instanceof BoardMemberId)) 
        	return false;
        
        BoardMemberId otherBoardMemberId = (BoardMemberId) obj;
        
        return Objects.equals(board, otherBoardMemberId.board) &&
               Objects.equals(user, otherBoardMemberId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, user);
    }
}
