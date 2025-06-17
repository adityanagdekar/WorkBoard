package com.project.workboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.entity.Board;
import com.project.workboard.repository.BoardRepository;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	
	@Autowired
	private BoardRepository boardRepository;
	
	@GetMapping("/getAll")
	public List<Board> getAllBoards() {
		return boardRepository.findAll();
	}

	@PostMapping("/create")
    public Board createBoard(@RequestBody Board board) {
		return boardRepository.save(board);  
	}
	
	@PostMapping("/delete")
	public boolean deleteBoard(@RequestBody Long boardId) {
		return false;
	}
}
