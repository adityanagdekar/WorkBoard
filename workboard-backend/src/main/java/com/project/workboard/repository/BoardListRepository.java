package com.project.workboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.workboard.dto.BoardListDTO;
import com.project.workboard.entity.BoardList;

@Repository
public interface BoardListRepository extends JpaRepository<BoardList, Integer>{
	@Query(value = """
		    SELECT 
		        id AS id,
		        board_id AS boardId,
		        name AS name,
		        created_by AS createdBy
		    FROM board_list
		    WHERE board_id = :boardId
		""", nativeQuery = true)
	List<BoardListDTO> getBoardListsByBoardId(@Param("boardId") int boardId);
}
