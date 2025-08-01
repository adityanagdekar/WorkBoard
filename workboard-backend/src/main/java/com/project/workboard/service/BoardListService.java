package com.project.workboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.BoardListDTO;
import com.project.workboard.dto.SavedBoardListDTO;
import com.project.workboard.dto.SavedTaskCardDTO;
import com.project.workboard.dto.SavedTaskMemberDTO;
import com.project.workboard.dto.TaskCardDTO;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardList;
import com.project.workboard.repository.BoardListRepository;
import com.project.workboard.repository.BoardRepository;
import com.project.workboard.repository.TaskCardRepository;
import com.project.workboard.repository.TaskMemberRepository;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class BoardListService {

	@Autowired
	private BoardListRepository boardListRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private TaskCardRepository taskCardRepository;
	
	@Autowired
	private TaskMemberRepository taskMemberRepository;

	public ResponseEntity<?> getLists(int boardId) {
		System.out.println("inside BoardListService -> getLists(int boardId), boardId: " + boardId);
		try {
			// checking boardId
			if (boardId <= 0) {
				throw new IllegalArgumentException("Invalid user ID received");
			}

			// getting board-lists based on boardId
			List<BoardListDTO> boardLists = boardListRepository.getBoardListsByBoardId(boardId);

			if (boardLists.size() > 0) {
				System.out.println("fetching task-cards");
				for (BoardListDTO boardListDTO : boardLists) {
					int listId = boardListDTO.getId();
					try {
						// Getting task-cards 
						List<SavedTaskCardDTO> taskCards = taskCardRepository.getTasksByListId(listId);
						
						System.out.println("no. of taskCards: " + taskCards.size());
						
						if (taskCards.size() > 0) {
							for(SavedTaskCardDTO taskCard: taskCards) {
								int cardId = taskCard.getId();
								try {
									// fetching members of the task-card
									List<SavedTaskMemberDTO> members = taskMemberRepository.getAllTaskMembers(cardId);
									
									// converting list to array
									SavedTaskMemberDTO[]  membersArr = members.toArray(new SavedTaskMemberDTO[0]);
									
									// setting task-members inside task-card
									taskCard.setMembers(membersArr);
								} catch (Exception e) {
									System.out.println("Exception while fetching members of task-card with id: " + cardId
											+ "\n Exception: " + e.getMessage());
									return ResponseEntity.status(HttpStatus.CONFLICT)
											.body("Error while fetching members of task-card with id: " + cardId);
								}
							}
						}
						
						// setting task-card inside board-list
						boardListDTO.setCards(taskCards.toArray(new SavedTaskCardDTO[0]));
					} catch (Exception e) {
						System.out.println("Exception while fetching task-cards for listId: " + listId
								+ "\n Exception: " + e.getMessage());
						return ResponseEntity.status(HttpStatus.CONFLICT)
								.body("Error while fetching task-cards for listId: " + listId);
					}
				}
			}

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = true;

			String responseMsg = (boardLists.size() > 0) ? "Board-lists fetched successfully"
					: "There are no board-lists present";

			ApiResponseDTO<List<BoardListDTO>> apiResponse = new ApiResponseDTO<List<BoardListDTO>>(successFlag,
					boardLists, responseMsg);

			return ResponseEntity.ok(apiResponse);
		} catch (IllegalArgumentException e) {
			System.out.println("Exception got invalid boardId: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Can't get board-lists, got invalid board-id. Login again");
		} catch (Exception e) {
			System.out.println(
					"Exception while fetching board-lists for boardId: " + boardId + "\n Exception: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Error while fetching board-lists for boardId: " + boardId);
		}
	}

	public ResponseEntity<?> saveBoardList(BoardListDTO boardListData, HttpServletResponse response) {
		System.out.println("Inside BoardListService: saveBoardList()");

		try {
			// Saving board-list
			SavedBoardListDTO savedBoardListData = new SavedBoardListDTO();

			// Setting boardList
			BoardList boardList;

			if (boardListData.getId() > 0) {
				// Update scenario — fetch existing board-list
				Optional<BoardList> existingOpt = boardListRepository.findById(boardListData.getId());
				if (existingOpt.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BoardList not found");
				}
				boardList = existingOpt.get();
			} else {
				// Insert scenario — create new board-list
				boardList = new BoardList();
			}

			// setting list-name
			boardList.setName(boardListData.getName());
			// setting list-createdBy
			boardList.setCreatedBy(boardListData.getUserId());

			// Getting board obj.
			Optional<Board> boardOpt = boardRepository.findById(boardListData.getBoardId());
			if (boardOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
			}
			Board board = (Board) boardOpt.get();
			boardList.setBoard(board);

			// Saving BoardList
			BoardList savedBoardList = boardListRepository.save(boardList);
			System.out.println("successfully saved board-list with id: " + savedBoardList.getId());

			// Setting SavedBoardListDTO obj.
			savedBoardListData.setId(savedBoardList.getId());
			savedBoardListData.setBoardId(savedBoardList.getBoard().getId());
			savedBoardListData.setName(savedBoardList.getName());

			// Data is saved successfully, let's send Api-Response for the same
			ApiResponseDTO<SavedBoardListDTO> apiResponse = new ApiResponseDTO<SavedBoardListDTO>(true,
					savedBoardListData, "Board-list saved successfully");

			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			System.out.println("Exception while saving board-list: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving board-list");
		}

	}

	@Transactional
	public ResponseEntity<?> deleteBoardList(Integer listId) {
		System.out.println("Inside BoardListService:: deleteBoardList, listId: " + listId);
		try {
			boardListRepository.deleteById(listId);

			System.out.println("Board-list deleted successfully with id: " + listId);

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String msg = "Board-list & task-cards deleted successfully";
			
			ApiResponseDTO<Integer> apiResponse = 
					new ApiResponseDTO<Integer>(successFlag, listId, msg);

			return ResponseEntity.ok(apiResponse);
			
		} catch (Exception e) {
			System.out.println("Exception while deleting board-list: " + e.getMessage());
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Error while deleting board-list, invalid list-id");
		}
	}

	private BoardListDTO getBoardListData(Integer listId) {
		System.out.println("inside BoardListService :: getBoardList(), listId: "+listId);
		
		BoardListDTO boardList = new BoardListDTO();
		try {
			// fetching board-list from Db
			boardList = boardListRepository.getBoardListData(listId);

			if (boardList != null) {
				System.out.println("fetching task-cards");

				List<SavedTaskCardDTO> taskCards = new ArrayList<>();
				
				try {
					// fetching task-cards from Db
					taskCards = taskCardRepository.getTasksByListId(listId);
					System.out.println("no. of taskCards: " + taskCards.size());
				} 
				catch (Exception e) {
					// issue while fetching task-cards from Db
					System.out.println("Exception while fetching task-cards for listId: " 
							+ listId + "\n Exception: "
							+ e.getMessage());
				}
				finally {
					// converting list to arr.
					SavedTaskCardDTO[] taskCardsArr = taskCards.toArray(new SavedTaskCardDTO[0]);
					// setting task-cards in boardList obj.
					boardList.setCards(taskCardsArr);
				}
			}
		} 
		catch (Exception e) {
			// issue while fetching board-list from Db
			System.out.println("Exception while fetching board-list: " + e.getMessage());
		}
		
		return boardList;
	}

}
