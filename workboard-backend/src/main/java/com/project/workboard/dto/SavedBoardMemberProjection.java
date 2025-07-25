package com.project.workboard.dto;

//projection interface or SavedBoardMemberDTO mapping
//this interface is later used in BoardRepository 
//when we r fetching boardMembers
public interface SavedBoardMemberProjection {
	int getUserId();
	int getRole();
	String getName(); 
}

