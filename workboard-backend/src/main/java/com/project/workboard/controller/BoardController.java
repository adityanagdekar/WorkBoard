package com.project.workboard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.workboard.dto.BoardDataDTO;
import com.project.workboard.entity.AppUser;
import com.project.workboard.entity.Board;
import com.project.workboard.entity.BoardMember;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.repository.BoardMemberRepository;
import com.project.workboard.repository.BoardRepository;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private AppUserRepository userRepository;
	
	@Autowired
	private BoardMemberRepository boardMemberRepository;
	
	@GetMapping("/getAll")
	public List<Board> getAllBoards() {
		return boardRepository.findAll();
	}

	@PostMapping("/save")
    public ResponseEntity<?> saveBoard(@RequestBody BoardDataDTO boardData) {
		System.out.println("board-data recieved");
		
		try {

			// get board-user id
			int userId = boardData.getUserId();
			
			// get AppUser obj.
			Optional<AppUser> userOpt = userRepository.findById(userId);
			if (userOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
			}
			AppUser user = (AppUser)userOpt.get();
			System.out.println("Board-creator-id: "+user.getId()+" board-creator-name: "+user.getName());
			
			// Setting board
			Board board = new Board();
			board.setName(boardData.getName());
			board.setDescription(boardData.getDescription());
			board.setUser(user);
			
			// Saving the board
			Board savedBoard = boardRepository.save(board);
			System.out.println("successfully saved board with id: "+board.getId());
			
			// Saving board-members as well
			for(BoardDataDTO.Member member: boardData.getMembers()) {
				int memberUserId = -1;
				try {
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
					AppUser memberUser = (AppUser)memberUserOpt.get();
					
					// setting boardMember
					BoardMember boardMember = new BoardMember();
					boardMember.setBoard(savedBoard);
					boardMember.setUser(memberUser);
					boardMember.setRole(member.getRole());
					
					// saving boardMember
					boardMemberRepository.save(boardMember);
					System.out.println("successfully saved board-member with id: "+
					boardMember.getUser().getId());
					
				} catch (Exception e) {
					System.out.println("Exception while saving board-member: "+e.getMessage());
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body("Error while saving board-member with id: "+memberUserId);
				}
				
			}
			return ResponseEntity.ok("Board data & members saved successfully");
			
		} catch (Exception e) {
			System.out.println("Exception while saving board-data: "+e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving board-data");
		}
	}
	
	@PostMapping("/delete")
	public boolean deleteBoard(@RequestBody Long boardId) {
		return false;
	}
}
