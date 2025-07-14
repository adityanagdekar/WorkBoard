package com.project.workboard.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.dto.SavedBoardDataDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardMember;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.repository.BoardMemberRepository;
import com.project.workboard.repository.BoardRepository;

@Component
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private BoardMemberRepository boardMemberRepository;

	public ResponseEntity<?> createBoard(BoardDataDTO boardData) {
		System.out.println("inside BoardService's createBoard, boardData: " + boardData.toString());

		int boardId = boardData.getBoardId();
		SavedBoardDataDTO savedBoardData = new SavedBoardDataDTO();
		try {
			// get board-user id
			int userId = boardData.getUserId();

			// get AppUser obj.
			Optional<AppUser> userOpt = userRepository.findById(userId);
			if (userOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			AppUser user = (AppUser) userOpt.get();
			System.out.println("Board-creator-id: " + user.getId() 
			+ " board-creator-name: " + user.getName());

			// Setting board
			Board board = new Board();
			board.setName(boardData.getName());
			board.setDescription(boardData.getDescription());
			board.setUser(user);

			// Saving the board
			Board savedBoard = boardRepository.save(board);
			boardId = savedBoard.getId();
			System.out.println("successfully saved board with id: " + boardId);

			// Saving board-id in savedBoardData obj.
			savedBoardData.setBoardId(boardId);

			int numBoardMembers = boardData.getMembers().length;
			int[] memberIds = new int[numBoardMembers];
			int boardMemberId = -1;

			if (boardId > 0) {
				try {
					// Saving board-members as well
					for (int i = 0; i < numBoardMembers; i++) {
						BoardDataDTO.Member member = boardData.getMembers()[i];

						int memberUserId = -1;

						System.out.println(member.toString());

						// checking if member is added or not
						if (!member.isAdded())
							continue;

						// getting AppUser obj. from member obj.
						memberUserId = member.getId();

						Optional<AppUser> memberUserOpt = userRepository.findById(memberUserId);
						if (memberUserOpt.isEmpty()) {
							return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
						}
						AppUser memberUser = (AppUser) memberUserOpt.get();

						// setting boardMember
						BoardMember boardMember = new BoardMember();
						boardMember.setBoard(savedBoard);
						boardMember.setUser(memberUser);
						boardMember.setRole(member.getRole());

						// Saving boardMember
						boardMemberRepository.save(boardMember);
						boardMemberId = boardMember.getUser().getId();
						System.out.println("successfully saved board-member with id: " + boardMemberId);

						// Saving board-member in memberIds arr.
						memberIds[i] = boardMemberId;

					}

				} catch (Exception e) {
					System.out.println("Exception while saving board-member: " + e.getMessage());
					System.out.println(
							"Need to delete the board, if saved. " + 
					"Can't save board without it 's board-memebrs");

					// delete the board, if not able to save board-members
					boardRepository.deleteById(boardId);

					return ResponseEntity
							.status(HttpStatus.UNAUTHORIZED)
							.body("Error while saving board-member with id: " + boardMemberId);
				}

				// Saving board-members-ids arr. in savedBoardData obj.
				savedBoardData.setMemberIds(memberIds);

			} else if (boardId < 0) {
				// board not saved successfully
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body("Error while saving board");
			}

		} catch (Exception e) {
			System.out.println("Exception while saving board-data: " + e.getMessage());
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Error while saving board-data");
		}

		// Data is saved successfully, let's send Api-Response for the same
		ApiResponseDTO<SavedBoardDataDTO> apiResponse = new 
				ApiResponseDTO<SavedBoardDataDTO>(true, 
						savedBoardData,
				"Board data & members saved successfully");
		
		return ResponseEntity.ok(apiResponse);
	}

	public ResponseEntity<?> updateBoard(BoardDataDTO boardData) {
		return ResponseEntity.ok("Board data & members saved successfully");
	}

}
