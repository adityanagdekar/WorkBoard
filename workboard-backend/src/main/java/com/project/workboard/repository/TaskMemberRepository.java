package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.workboard.dto.SavedTaskMemberDTO;
import com.project.workboard.entity.TaskMember;
import com.project.workboard.entity.TaskMemberId;

public interface TaskMemberRepository extends JpaRepository<TaskMember, TaskMemberId> {

	@Query(value = """
		    SELECT 
		        user_id AS userId,
		        role AS role
	        FROM task_member
	        WHERE card_id = :cardId 
	""", nativeQuery = true)
	List<SavedTaskMemberDTO> getAllTaskMembers(@Param("cardId") int cardId);
}
