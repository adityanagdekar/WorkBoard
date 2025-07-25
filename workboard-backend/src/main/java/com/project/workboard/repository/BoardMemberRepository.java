package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.workboard.dto.SavedBoardMemberDTO;
import com.project.workboard.dto.SavedBoardMemberProjection;
import com.project.workboard.entity.BoardMember;
import com.project.workboard.entity.BoardMemberId;

public interface BoardMemberRepository extends JpaRepository<BoardMember, BoardMemberId> {

	@Query(value = """
		    SELECT 
		        bm.user_id AS userId,
		        bm.role AS role,
		        au.name AS name
	        FROM board_member as bm
	        JOIN app_user as au
	        ON bm.user_id = au.id 
	        WHERE bm.board_id = :boardId 
	""", nativeQuery = true)
	List<SavedBoardMemberProjection> getAllBoardMembers(@Param("boardId") int boardId);
	
}
