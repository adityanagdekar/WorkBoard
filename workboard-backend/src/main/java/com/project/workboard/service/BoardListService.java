package com.project.workboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.BoardListDTO;
import com.project.workboard.dto.SavedBoardDataDTO;
import com.project.workboard.repository.BoardListRepository;

@Component
public class BoardListService {

	@Autowired
	private BoardListRepository boardListRepository;

	public ResponseEntity<?> getLists(int boardId) {
		System.out.println("inside BoardListService -> getLists(int boardId), boardId: " + boardId);
		try {
			// checking boardId
			if (boardId <= 0) {
				throw new IllegalArgumentException("Invalid user ID received");
			}

			// getting board-lists based on boardId
			List<BoardListDTO> boardLists = boardListRepository.getBoardListsByBoardId(boardId);

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String responseMsg = (boardLists.size() > 0) ? 
					"Board-lists fetched saved successfully" : 
						"There are no board-lists present";
			ApiResponseDTO<List<BoardListDTO>> apiResponse = 
					new ApiResponseDTO<List<BoardListDTO>>(
					successFlag, boardLists, responseMsg);

			return ResponseEntity.ok(apiResponse);
		} catch (IllegalArgumentException e) {
			System.out.println("Exception got invalid boardId: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Can't get board-lists, got invalid board-id. Login again");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok("boardId: " + boardId + " received successfully for fetching board-lists");
	}

}
