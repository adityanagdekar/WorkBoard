package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.workboard.dto.TaskCardDTO;
import com.project.workboard.entity.TaskCard;

@Repository
public interface TaskCardRepository extends JpaRepository<TaskCard, Integer>{
	@Query(value = """
		    SELECT id as id, 
		    name as name, 
		    description as desc, 
		    is_active as isActive, 
		    is_completed as isCompleted 
		    FROM task_card 
		    WHERE list_id=:listId
		""", nativeQuery = true)
	List<TaskCardDTO> getTasksByListId(@Param("listId") int listId);
}
