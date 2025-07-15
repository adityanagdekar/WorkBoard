package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.workboard.dto.BoardWithMembersProjection;
import com.project.workboard.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
	
	@Query(value = "SELECT b.id AS boardId, bm.user_id AS userId " +
            "FROM board b INNER JOIN board_member bm ON b.id = bm.board_id", nativeQuery = true)
	List<BoardWithMembersProjection> findBoardsWithMembers();
}
