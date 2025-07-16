package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.workboard.dto.BoardWithMembersProjection;
import com.project.workboard.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
	
	@Query(value = """
		    SELECT 
		        b.id AS boardId,
		        bm.user_id AS userId,
		        b.name AS boardName,
		        b.board_desc AS boardDesc,
		        bm.role AS role
		    FROM board b
		    JOIN board_member bm ON b.id = bm.board_id
		    WHERE b.user_id = :userId OR bm.user_id = :userId 
		""", nativeQuery = true)
	List<BoardWithMembersProjection> findBoardsWithMembers(@Param("userId") int loggedIn_userId);
}
