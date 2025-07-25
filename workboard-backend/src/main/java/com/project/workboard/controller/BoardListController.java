package com.project.workboard.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.BoardListDTO;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardList;
import com.project.workboard.repository.BoardListRepository;
import com.project.workboard.security.JwtService;
import com.project.workboard.service.BoardListService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/list")
public class BoardListController {
	@Autowired
	private BoardListService boardListService;
	
	@GetMapping("/lists/{boardId}")
	public ResponseEntity<?> getBoardLists(HttpServletRequest request, @PathVariable int boardId){
		return boardListService.getLists(boardId);
	}
	
	@PostMapping("/save")
    public ResponseEntity<?> saveBoardList(@RequestBody BoardListDTO boardListData,  
			HttpServletResponse response) {
		System.out.println("Inside BoardListController: saveBoardList");
		return boardListService.saveBoardList(boardListData, response);  
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> deleteBoardList(@RequestBody Map<String, Integer> payload) {
		Integer id = payload.get("id");
		System.out.println("Inside BoardListController:: deleteBoardList, id: "+id);
		return boardListService.deleteBoardList(id);
	}
}
