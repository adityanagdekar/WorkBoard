package com.project.workboard.dto;

// projection interface or SavedBoardDataDTO mapping
// this interface is later used in BoardRepository 
// when we r fetching board-info along with board-members
public interface BoardWithMembersProjection {
	Integer getBoardId();
	Integer getUserId();
	String getBoardName();
	String getBoardDesc();
	Integer getRole();
}
