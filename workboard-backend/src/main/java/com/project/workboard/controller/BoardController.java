package com.project.workboard.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/boards")
	public ResponseEntity<?>getAllBoards(HttpServletRequest request) {
		// getting logged-in user's Id from the JWT token
		int loggedIn_userId = boardService.getLoggedInUserId(request);
		return boardService.getBoardsWithMembersIds(loggedIn_userId);
	}
	
	/*@GetMapping("/{boardId}")
	public ResponseEntity<?> getBoard(HttpServletRequest request, @PathVariable int boardId) {
		// getting logged-in user's Id from the JWT token
	    int loggedIn_userId = boardService.getLoggedInUserId(request);
	    return boardService.getBoardData(boardId, loggedIn_userId);
	}*/

	@PostMapping("/save")
    public ResponseEntity<?> saveBoard(@RequestBody BoardDataDTO boardData) {
		System.out.println("boardId: "+boardData.getBoardId());
//		if (boardData.getBoardId() == -1)
			return boardService.createBoard(boardData);
//		else
//			return boardService.updateBoard(boardData);
		
		// debugging purposes
//		return ResponseEntity
//				.status(HttpStatus.UNAUTHORIZED)
//				.body("Error while saving board-data");
	}
	
	// PENDING
	@PostMapping("/delete")
	public boolean deleteBoard(@RequestBody Long boardId) {
		return false;
	}
}
