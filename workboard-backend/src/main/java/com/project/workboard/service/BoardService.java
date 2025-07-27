package com.project.workboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.dto.BoardWithMembersProjection;
import com.project.workboard.dto.SavedBoardDataDTO;
import com.project.workboard.dto.SavedBoardDataDTO.MemberDataDTO;
import com.project.workboard.dto.SavedBoardMemberDTO;
import com.project.workboard.dto.SavedBoardMemberProjection;
import com.project.workboard.entity.AppUser;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardMember;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.repository.BoardMemberRepository;
import com.project.workboard.repository.BoardRepository;
import com.project.workboard.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private BoardMemberRepository boardMemberRepository;

	@Autowired
	private JwtService jwtService;

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
			System.out.println("Board-creator-id: " + user.getId() + " board-creator-name: " + user.getName());

			// Setting board
			Board board = new Board();
			board.setName(boardData.getName());
			board.setDescription(boardData.getDescription());
			board.setUser(user);

			// Saving the board
			Board savedBoard = boardRepository.save(board);
			boardId = savedBoard.getId();
			System.out.println("successfully saved board with id: " + boardId);

			// Saving board-info in savedBoardData obj.
			savedBoardData.setBoardId(boardId);
			savedBoardData.setBoardName(boardData.getName());
			savedBoardData.setBoardDesc(boardData.getDescription());

			int numBoardMembers = boardData.getMembers().length;
			MemberDataDTO[] membersDataDTOs = new MemberDataDTO[numBoardMembers];
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

						// Saving member-data-dto in memberIds arr.
						membersDataDTOs[i] = new MemberDataDTO(boardMemberId, member.getRole());

					}

				} catch (Exception e) {
					System.out.println("Exception while saving board-member: " + e.getMessage());
					System.out.println(
							"Need to delete the board, if saved. " + "Can't save board without it's board-members");

					// delete the board, if not able to save board-members
					boardRepository.deleteById(boardId);

					return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body("Error while saving board-member with id: " + boardMemberId);
				}

				// Saving members-data-dto arr. in savedBoardData obj.
				savedBoardData.setMembers(membersDataDTOs);

			} else if (boardId < 0) {
				// board not saved successfully
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving board");
			}
			
			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = (boardId > 0 ? true : false);
			String msg = (boardId > 0 ? "Board data & members saved successfully" : "Error in saving board data & members");
			
			ApiResponseDTO<SavedBoardDataDTO> apiResponse = new 
					ApiResponseDTO<SavedBoardDataDTO>(successFlag, savedBoardData, msg);

			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			System.out.println("Exception while saving board-data: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving board-data");
		}
	}

	// PENDING
	public ResponseEntity<?> updateBoard(BoardDataDTO boardData) {
		return ResponseEntity.ok("Board data & members saved successfully");
	}

	// PENDING
	public ResponseEntity<?> deleteBoard(Integer boardData) {
		return ResponseEntity.ok("Board data & members saved successfully");
	}

	public ResponseEntity<?> getBoardsWithMembersIds(int loggedIn_userId) {
		try {
			if (loggedIn_userId <= 0) {
				throw new IllegalArgumentException("Invalid user ID received");
			}

			// getting data from the db
			List<BoardWithMembersProjection> rawData = boardRepository.findBoardsWithMembers(loggedIn_userId);

			// forming memeber-ids-map -> {boardId: [memberIds....]}
			// forming map to hold board-info -> {boardId: {name, desc, board-id, user-id,
			// role}, ....}
			Map<Integer, List<MemberDataDTO>> memberIdsMap = new HashMap<>();
			Map<Integer, BoardWithMembersProjection> boardProjectionMap = new HashMap<>();

			for (BoardWithMembersProjection row : rawData) {
				int boardId = row.getBoardId();
				int userId = row.getUserId();
				int userRole = row.getRole();
				memberIdsMap.computeIfAbsent(boardId, k -> new ArrayList<MemberDataDTO>())
						.add(new MemberDataDTO(userId, userRole));

				boardProjectionMap.putIfAbsent(boardId, row);
			}

			// getting results
			List<SavedBoardDataDTO> boardsWithMemberIds = getSavedBoardDataMappings(memberIdsMap, boardProjectionMap);

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String responseMsg = "Board fetched saved successfully";
			ApiResponseDTO<List<SavedBoardDataDTO>> apiResponse = new ApiResponseDTO<List<SavedBoardDataDTO>>(
					successFlag, boardsWithMemberIds, responseMsg);

			return ResponseEntity.ok(apiResponse);

		} catch (IllegalArgumentException e) {
			System.out.println("Exception while getting user-id from JWT token: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Can't get user-id so can't fetch boards, login again");
		} catch (Exception e) {
			System.out.println("Exception while fetching board-data with member ids: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Exception while fetching board-data with member ids");
		}

	}

	private List<SavedBoardDataDTO> getSavedBoardDataMappings(Map<Integer, List<MemberDataDTO>> memberIdsMap,
			Map<Integer, BoardWithMembersProjection> boardProjectionMap) {

		List<SavedBoardDataDTO> resultBoardDataDTOs = new ArrayList<SavedBoardDataDTO>();
		for (Map.Entry<Integer, List<MemberDataDTO>> entry : memberIdsMap.entrySet()) {
			int boardId = entry.getKey();
			List<MemberDataDTO> memberDataDTOList = entry.getValue();

			// getting memberIds arr. from List of member-ids.
			int numOfMembers = memberDataDTOList.size();
			MemberDataDTO[] members = new MemberDataDTO[numOfMembers];

			for (int i = 0; i < numOfMembers; i++) {
				members[i] = memberDataDTOList.get(i);
			}

			// this obj. contains board-info -> {name, desc, board-id, user-id, role}
			BoardWithMembersProjection projection = boardProjectionMap.get(boardId);

			// setting SavedBoardDataDTO obj.
			SavedBoardDataDTO boardData = new SavedBoardDataDTO();
			boardData.setBoardId(boardId);
			boardData.setMembers(members);
			boardData.setBoardName(projection.getBoardName());
			boardData.setBoardDesc(projection.getBoardDesc());

//			System.out.println("\n boardData: "+boardData.toString()+"\n");

			resultBoardDataDTOs.add(boardData);
		}

		return resultBoardDataDTOs;
	}

	public int getLoggedInUserId(HttpServletRequest request) {
		String jwtTokenString = jwtService.getTokenFromRequest(request);
		int userId = jwtService.getUserIdFromJWT(jwtTokenString);
		return userId;
	}

	public ResponseEntity<?> getAllBoardMembers(int boardId) {
		System.out.println("Inside BoardService :: getAllBoardMembers, boardId: " + boardId);
		try {

			if (boardId <= 0) {
				throw new IllegalArgumentException("Invalid user ID received");
			}

			List<SavedBoardMemberProjection> rawData = boardMemberRepository.getAllBoardMembers(boardId);
//			System.out.println("rawData.len: "+rawData.size());
			
			List<SavedBoardMemberDTO> boardMembers = new ArrayList<>();

			for (SavedBoardMemberProjection projection : rawData) {
				SavedBoardMemberDTO savedBoardMember = new 
						SavedBoardMemberDTO(
								projection.getUserId(), 
								projection.getRole(), 
								projection.getName());
				boardMembers.add(savedBoardMember);
				System.out.println(savedBoardMember.toString());

			}
			

			// Data is fetched successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String responseMsg = "Board-Members fetched successfully";

			ApiResponseDTO<List<SavedBoardMemberDTO>> apiResponse = new ApiResponseDTO<List<SavedBoardMemberDTO>>(
					successFlag, boardMembers, responseMsg);

			return ResponseEntity.ok(apiResponse);
		} catch (IllegalArgumentException e) {
			System.out.println("Exception while getting all board-members: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid board-id, so can't get board-members");
		} catch (Exception e) {
			System.out.println("Exception while fetching board-members: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while fetching board-members");
		}
	}

}
