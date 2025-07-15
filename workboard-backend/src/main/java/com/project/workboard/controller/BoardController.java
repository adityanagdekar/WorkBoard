package com.project.workboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.dto.SavedBoardDataDTO;
import com.project.workboard.service.BoardService;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/getAll")
	public List<SavedBoardDataDTO> getAllBoards() {
		return boardService.getBoardsWithMembersIds();
	}

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
	
	@PostMapping("/delete")
	public boolean deleteBoard(@RequestBody Long boardId) {
		return false;
	}
}
