package com.project.workboard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardList;
import com.project.workboard.repository.BoardListRepository;
import com.project.workboard.service.BoardListService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/list")
public class BoardListController {
	@Autowired
	private BoardListService boardListService;
	
	@GetMapping("/lists/{boardId}")
	public ResponseEntity<?> getBoardLists(HttpServletRequest request, @PathVariable int boardId){
		return boardListService.getLists(boardId);
	}
	
	@PostMapping("/create")
    public BoardList createBoardList(@RequestBody BoardList boardList) {
		return null;  
	}
	
	@PostMapping("/delete")
	public boolean deleteBoardList(@RequestBody Long id) {
		return false;
	}
}
