package com.project.workboard.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.TaskDataDTO;
import com.project.workboard.entity.BoardList;
import com.project.workboard.entity.TaskCard;
import com.project.workboard.repository.TaskCardRepository;
import com.project.workboard.service.TaskCardService;

@RestController
@RequestMapping("/api/task")
public class TaskCardController {
	@Autowired
	private TaskCardService taskCardService;
	
	@GetMapping("/getAllTasks")
	private List<TaskCard> getTaskCards(@RequestBody Long boardId, Long boardListId){
		return null;
	}
	
	@PostMapping("/save")
    public ResponseEntity<?> saveTaskCard(@RequestBody TaskDataDTO taskData) {
		return taskCardService.saveTaskCard(taskData);  
	}
	
	@PostMapping("/delete")
	private ResponseEntity<?> deleteTaskCard(@RequestBody Map<String, Integer> payload) {
		Integer id = payload.get("id");
		return taskCardService.deleteTaskCard(id);
	}
}
