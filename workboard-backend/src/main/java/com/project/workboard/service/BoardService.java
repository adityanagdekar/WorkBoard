package com.project.workboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.dto.BoardEvent;
import com.project.workboard.dto.BoardWithMembersProjection;
import com.project.workboard.dto.SavedBoardDataDTO;
import com.project.workboard.dto.SavedBoardDataDTO.MemberDataDTO;
import com.project.workboard.dto.SavedBoardMemberDTO;
import com.project.workboard.dto.SavedBoardMemberProjection;
import com.project.workboard.dto.SavedTaskMemberDTO;
import com.project.workboard.dto.TaskDataDTO;
import com.project.workboard.dto.UpdatedBoardSummaryDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardList;
import com.project.workboard.entity.BoardMember;
import com.project.workboard.entity.TaskCard;
import com.project.workboard.entity.TaskMember;
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

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public BoardService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public ResponseEntity<?> saveBoard(BoardDataDTO boardData) {
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
			String msg = (boardId > 0 ? "Board data & members saved successfully"
					: "Error in saving board data & members");

			ApiResponseDTO<SavedBoardDataDTO> apiResponse = new ApiResponseDTO<SavedBoardDataDTO>(successFlag,
					savedBoardData, msg);

			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			System.out.println("Exception while saving board-data: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving board-data");
		}
	}

	public ResponseEntity<?> updateBoard(BoardDataDTO boardData) {
		System.out.println("Inside BoardService :: updateBoard, boardData: " + boardData.toString());

		try {
			// Getting board
			Optional<Board> boardOpt = boardRepository.findById(boardData.getBoardId());
			if (boardOpt.isEmpty()) {
				throw new IllegalArgumentException("Board not found");
			}
			Board board = boardOpt.get();
			System.out.println("board: " + board.toString());

			// checking name boolean
			boolean boardChanged = false;
			if (!Objects.equals(board.getName(), boardData.getName())) {
				System.out.println("board-name updated to : " + boardData.getName());
				board.setName(boardData.getName());
				boardChanged = true;
			}

			// checking description
			if (!Objects.equals(board.getDescription(), boardData.getDescription())) {
				System.out.println("board-description updated to : " + boardData.getDescription());
				board.setDescription(boardData.getDescription());
				boardChanged = true;
			}

			// checking members
			boolean memberChanged = false;
			BoardDataDTO.Member[] members = boardData.getMembers();
			if (members.length > 0) {
				// Checking if updates are made to Board-members
				memberChanged = saveUpdatesForBoardMembers(boardData, board);
				if (memberChanged)
					boardChanged = true;
			}

			String msg = "Board data & members saved successfully";
			if (boardChanged) {
				// SAVING the updates to the board
				board = boardRepository.save(board);

				// NOTIFYING OTHER USERS ABOUT THE UPDATE
				UpdatedBoardSummaryDTO boardSummary = new UpdatedBoardSummaryDTO(board.getId(), board.getName(),
						board.getDescription());

				// BROADCAST to all viewers of this board
				notifyBoardViewers(board.getId(), boardSummary, 1);

				msg = "Successfully saved updates to board with id: " + board.getId() + " & name: " + board.getName();
				System.out.println(msg);
			} else {
				msg = "No changes were made to board with id: " + board.getId() + " & name: " + board.getName();
				System.out.println(msg);
			}

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = true;

			ApiResponseDTO<BoardDataDTO> apiResponse = new ApiResponseDTO<>(successFlag, boardData, msg);

			return ResponseEntity.ok(apiResponse);

		} catch (IllegalArgumentException e) {
			System.out.println("Exception with board-data: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while updating board-data");
		} catch (Exception e) {
			System.out.println("Exception while saving updates to board-data: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while saving updated board-data");
		}
	}

	private void notifyBoardViewers(int boardId, Object payload, int flag) {
		BoardEvent evt = new BoardEvent();
		evt.setType((flag == 1) ? BoardEvent.Type.UPSERT : BoardEvent.Type.DELETE);
		evt.setBoardId(boardId);
		evt.setPayload(payload);
		evt.setVersion(System.currentTimeMillis());

		String dest = "/topic/board." + boardId;
		messagingTemplate.convertAndSend(dest, evt);
	}

	public ResponseEntity<?> testEvent() {
		System.out.println("Inside BoardService :: testEvent");
		try {
			BoardEvent evt = new BoardEvent();
			evt.setType(BoardEvent.Type.UPSERT);
			evt.setBoardId(10);

			Map<String, Object> payload = new HashMap<>();
			payload.put("id", 10);
			payload.put("name", "Test Name");
			payload.put("description", "Updated via test");
			evt.setPayload(payload);

			messagingTemplate.convertAndSend("/topic/board.10", evt);

			// Data is saved successfully, let's send Api-Response for the same
			ApiResponseDTO<Integer> apiResponse = new ApiResponseDTO<>(true, 1, "testing");
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			System.out.println("Exception while testing live-updates via socket-connection: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error while testing live-updates via socket-connection");
		}
	}

	private boolean saveUpdatesForBoardMembers(BoardDataDTO boardData, Board board) {
		System.out.println("inside BoardService :: saveUpdatesForBoardMembers");

		// getting existing members of the task
		List<SavedBoardMemberDTO> existingMembersList = getSavedBoardMembers(boardData.getBoardId());
		// mapping user-id to role for existing-members
		Map<Integer, Integer> existingMembersMap = existingMembersList.stream()
				.collect(Collectors.toMap(SavedBoardMemberDTO::getId, SavedBoardMemberDTO::getRole));

		// getting updated-members of the task from taskDataDTO
		BoardDataDTO.Member[] updatedMembersArr = boardData.getMembers();

		// mapping user-id to role for updated-members
		Map<Integer, Integer> updatedMembersMap = new HashMap<>();
		for (BoardDataDTO.Member member : updatedMembersArr) {
			updatedMembersMap.put(member.getId(), member.getRole());
		}

		// Lists for various purposes
		List<BoardMember> addList = new ArrayList<>();
		List<BoardMember> removeList = new ArrayList<>();
		List<BoardMember> updateList = new ArrayList<>();

		// boolean flag to detect change
		boolean memberChanged = false;

		// Checking existing-members
		// To detect existing-members to be removed or updated
		for (SavedBoardMemberDTO existingMember : existingMembersList) {
			int userId = existingMember.getId();
			int existingRole = existingMember.getRole();

			// creating TaskMember obj.
			BoardMember boardMember = new BoardMember();

			// getting app-user
			AppUser user = userRepository.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("User not found"));

			// setting board-member
			boardMember.setUser(user);
			boardMember.setBoard(board);
			boardMember.setRole(existingRole);

			// checking in updatedMembersMap
			if (!updatedMembersMap.containsKey(userId)) {
				// existing-member needs to be Removed

				// add to removeList
				removeList.add(boardMember);
				
//				memberChanged = true;
			} else {
				// existingMember is present in updatedMemberMap
				// let's check for change in role
				// getting new-role
				int newRole = updatedMembersMap.get(userId);

				// checking new-role & existing-role
				if (!Objects.equals(existingRole, newRole)) {
					// Role changed
					boardMember.setRole(newRole);

					// add to updateList
					updateList.add(boardMember);

//					memberChanged = true;
				}
			}
		}

		// Checking new-members to
		// Detect new-members to be added
		for (Map.Entry<Integer, Integer> mapEntry : updatedMembersMap.entrySet()) {
			int userId = mapEntry.getKey();
			int role = mapEntry.getValue();

			// new-member should not exist in existingMembersMap
			if (!existingMembersMap.containsKey(userId)) {
				// this is true, so it means -> add the new-member to the addList

				// creating TaskMember obj.
				BoardMember boardMember = new BoardMember();

				// getting app-user
				AppUser user = userRepository.findById(userId)
						.orElseThrow(() -> new IllegalArgumentException("User not found"));

				// setting task-member
				boardMember.setUser(user);
				boardMember.setBoard(board);
				boardMember.setRole(role);

				// pushing to addList
				addList.add(boardMember);

//				memberChanged = true;
			}
		}

		// saving the updates to Db
		if (addList.size() > 0) {
			System.out.println("board-members to add: ");
			for (BoardMember bm : addList) {
				System.out.println(bm.toString());
			}
			boardMemberRepository.saveAll(addList);
			memberChanged = true;
		}

		if (updateList.size() > 0) {
			System.out.println("board-members to update: ");
			for (BoardMember bm : updateList) {
				System.out.println(bm.toString());
			}
			boardMemberRepository.saveAll(updateList);
			memberChanged = true;
		}

		if (removeList.size() > 0) {
			System.out.println("board-members to remove: ");
			for (BoardMember bm : removeList) {
				System.out.println(bm.toString());
			}
			boardMemberRepository.deleteAll(removeList);
			memberChanged = true;
		}

		if (memberChanged)
			System.out.println("Updates to board-members are saved successfully");
		else
			System.out.println("No changes were made to the board-members of the board");
		return memberChanged;
	}

	@Transactional
	public ResponseEntity<?> deleteBoard(Integer boardId) {
		System.out.println("Inside BoardService :: deleteBoard, boardId: " + boardId);
		try {
			boardRepository.deleteById(boardId);

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String msg = "Board deleted successfully";

			ApiResponseDTO<Integer> apiResponse = new ApiResponseDTO<Integer>(successFlag, boardId, msg);

			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			System.out.println("Exception while deleting board : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while deleting board , invalid id");
		}
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
				memberIdsMap.computeIfAbsent(boardId, k -> 
					new ArrayList<MemberDataDTO>()).add(new MemberDataDTO(userId, userRole));

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
			int boardCreatorId = -1;
			List<MemberDataDTO> memberDataDTOList = entry.getValue();

			// getting memberIds arr. from List of member-ids.
			int numOfMembers = memberDataDTOList.size();
			MemberDataDTO[] members = new MemberDataDTO[numOfMembers];

			for (int i = 0; i < numOfMembers; i++) {
				members[i] = memberDataDTOList.get(i);

				// getting board-creator's userId
				if (members[i].getMemberRole() == 1) {
					boardCreatorId = members[i].getMemberId();
				}
			}

			// this obj. contains board-info -> {name, desc, board-id, user-id, role}
			BoardWithMembersProjection projection = boardProjectionMap.get(boardId);

			// setting SavedBoardDataDTO obj.
			SavedBoardDataDTO boardData = new SavedBoardDataDTO();
			boardData.setBoardId(boardId);
			boardData.setMembers(members);
			boardData.setBoardName(projection.getBoardName());
			boardData.setBoardDesc(projection.getBoardDesc());
			boardData.setUserId(boardCreatorId);

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

	private List<SavedBoardMemberDTO> getSavedBoardMembers(int boardId) {
		System.out.println("inside BoardService :: getSavedBoardMembers, boardId: " + boardId);
		List<SavedBoardMemberProjection> rawData = boardMemberRepository.getAllBoardMembers(boardId);

		List<SavedBoardMemberDTO> boardMembers = new ArrayList<>();

		for (SavedBoardMemberProjection projection : rawData) {
			SavedBoardMemberDTO savedBoardMember = new SavedBoardMemberDTO(projection.getUserId(), projection.getRole(),
					projection.getName());
			boardMembers.add(savedBoardMember);
		}
		return boardMembers;
	}

	public ResponseEntity<?> getAllBoardMembers(int boardId) {
		System.out.println("Inside BoardService :: getAllBoardMembers, boardId: " + boardId);
		try {

			if (boardId <= 0) {
				throw new IllegalArgumentException("Invalid user ID received");
			}

			List<SavedBoardMemberDTO> boardMembers = getSavedBoardMembers(boardId);

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
