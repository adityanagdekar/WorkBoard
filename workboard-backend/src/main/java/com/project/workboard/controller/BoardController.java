package com.project.workboard.controller;

import java.util.Map;

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
	public ResponseEntity<?> getAllBoards(HttpServletRequest request) {
		// getting logged-in user's Id from the JWT token
		int loggedIn_userId = boardService.getLoggedInUserId(request);
		return boardService.getBoardsWithMembersIds(loggedIn_userId);
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveBoard(@RequestBody BoardDataDTO boardData) {
		System.out.println("Inside BoardController :: boardData: " + boardData.getBoardId());
		if (boardData.getBoardId() == -1)
			return boardService.saveBoard(boardData);
		else
			return boardService.updateBoard(boardData);

	}
	
	@GetMapping("/test/board-event")
	public ResponseEntity<?> testEvent() {
		System.out.println("Inside BoardController :: testEvent");
		return boardService.testEvent();
	}

	@PostMapping("/delete")
	public ResponseEntity<?> deleteBoard(@RequestBody Map<String, Integer> payload) {
		Integer id = payload.get("id");
		return boardService.deleteBoard(id);
	}

	@GetMapping("/members/{boardId}")
	public ResponseEntity<?> getAllBoardMembers(HttpServletRequest request, @PathVariable int boardId) {
		return boardService.getAllBoardMembers(boardId);
	}
}
