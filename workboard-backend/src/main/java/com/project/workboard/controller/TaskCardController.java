package com.project.workboard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.entity.BoardList;
import com.project.workboard.entity.TaskCard;
import com.project.workboard.repository.TaskCardRepository;

@RestController
@RequestMapping("/api/task")
public class TaskCardController {
	@Autowired
	private TaskCardRepository taskCardRepository;
	
	@GetMapping("/getAllTasks")
	private List<TaskCard> getTaskCards(@RequestBody Long boardId, Long boardListId){
		return null;
	}
	
	@PostMapping("/create")
    public TaskCard createTaskCard(@RequestBody TaskCard taskCard) {
		return null;  
	}
	
	@PostMapping("/delete")
	private boolean deleteTaskCard(@RequestBody Long id) {
		return false;
	}
}
