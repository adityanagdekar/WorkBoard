package com.project.workboard.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardListService {

	public ResponseEntity<?> getLists(int boardId) {
		System.out.println("inside BoardListService -> getLists(int boardId), boardId: "+boardId);
		return ResponseEntity.ok("boardId: "+boardId+" received successfully for fetching board-lists");
	}

}
