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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.SavedTaskCardDTO;
import com.project.workboard.dto.TaskDataDTO;
import com.project.workboard.dto.SavedTaskMemberDTO;
import com.project.workboard.dto.TaskDataDTO.Member;
import com.project.workboard.entity.AppUser;
import com.project.workboard.entity.BoardList;
import com.project.workboard.entity.TaskCard;
import com.project.workboard.entity.TaskMember;
import com.project.workboard.repository.AppUserRepository;
import com.project.workboard.repository.BoardListRepository;
import com.project.workboard.repository.TaskCardRepository;
import com.project.workboard.repository.TaskMemberRepository;

@Component
public class TaskCardService {

	@Autowired
	private TaskCardRepository taskCardRepository;

	@Autowired
	private BoardListRepository boardListRepository;

	@Autowired
	private AppUserRepository userRepository;

	@Autowired
	private TaskMemberRepository taskMemberRepository;

	public ResponseEntity<?> saveTaskCard(TaskDataDTO taskData) {
		System.out.println("Inside TaskCardService :: saveTaskCard, taskData: " + taskData.toString() + "\n");

		try {
			SavedTaskCardDTO savedTaskCardDTO = new SavedTaskCardDTO();

			TaskCard taskCard;

			if (taskData.getId() > 0) {
				// Update scenario — fetch existing task-card
				Optional<TaskCard> existingOpt = taskCardRepository.findById(taskData.getId());

				if (existingOpt.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task-Card not found");
				}
				taskCard = existingOpt.get();
			} else {
				// Insert scenario — create new task-card
				taskCard = new TaskCard();
			}

			// Setting task-card
			taskCard.setName(taskData.getName());
			taskCard.setDescription(taskData.getDescription());
			taskCard.setCompleted(taskData.isCompleted());
			taskCard.setActive(taskData.isActive());
			taskCard.setCreatedBy(taskData.getUserId());

			// Getting board-list obj.
			Optional<BoardList> boardListOpt = boardListRepository.findById(taskData.getListId());
			if (boardListOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board-list not found");
			}
			BoardList boardList = (BoardList) boardListOpt.get();
			taskCard.setBoardList(boardList);

			// Saving TaskCard
			TaskCard savedTaskCard = taskCardRepository.save(taskCard);
			int taskCardId = savedTaskCard.getId();
			System.out.println("successfully saved task-card with id: " + taskCardId);

			if (taskCardId > 0) {

				// Setting SavedTaskCardDTO obj.
				savedTaskCardDTO.setId(taskCardId);
				savedTaskCardDTO.setName(savedTaskCard.getName());
				savedTaskCardDTO.setDescription(savedTaskCard.getDescription());
				savedTaskCardDTO.setActive(savedTaskCard.isActive());
				savedTaskCardDTO.setCompleted(savedTaskCard.isCompleted());

				// this means taskCard is saved successfully, let's save the task-members as
				// well
				try {
					// Getting task-members
					Member[] taskMembers = taskData.getMembers();
					int numTaskMembers = taskMembers.length;

					// this list. is later stored in our savedTaskCardDTO obj.
					List<SavedTaskMemberDTO> taskMemberDataList = new ArrayList<>();

					// Saving board-members as well
					for (int i = 0; i < numTaskMembers; i++) {
						// BoardDataDTO.Member member = boardData.getMembers()[i];
						Member fetchedTaskMember = taskMembers[i];

						int taskMemberId = -1;

						System.out.println(fetchedTaskMember.toString());

						// checking if member is added or not
						if (!fetchedTaskMember.isAdded())
							continue;

						// getting AppUser obj. from member obj.
						taskMemberId = fetchedTaskMember.getId();

						Optional<AppUser> memberUserOpt = userRepository.findById(taskMemberId);
						if (memberUserOpt.isEmpty()) {
							return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
						}
						AppUser memberUser = (AppUser) memberUserOpt.get();

						// setting taskMember
						TaskMember taskMember = new TaskMember();
						taskMember.setTaskCard(savedTaskCard);
						taskMember.setUser(memberUser);
						taskMember.setRole(fetchedTaskMember.getRole());

						// Saving boardMember
						taskMemberRepository.save(taskMember);
						taskMemberId = taskMember.getUser().getId();
						System.out.println("successfully saved task-member with id: " + taskMemberId + "\n");

						// Saving member-data-dto in memberIds arr.
						taskMemberDataList.add(new SavedTaskMemberDTO(taskMemberId, fetchedTaskMember.getRole()));

					}

					// once we have processed all task-members, save the arr. of task-members
					savedTaskCardDTO.setMembers(taskMemberDataList.toArray(new SavedTaskMemberDTO[0]));

				} catch (Exception e) {
					System.out.println("Exception while saving task-member: " + e.getMessage());
					System.out.println(
							"Need to delete the board, if saved. " + "Can't save task without it's task-members");

					// delete the board, if not able to save board-members
					taskCardRepository.deleteById(taskCardId);

					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving task-member");
				}

			} else if (taskCardId < 0) {
				// task-card not saved successfully
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Error while saving task-card, invalid task-card id");
			}

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = (taskCardId > 0 ? true : false);
			String msg = (taskCardId > 0 ? "Task data & members saved successfully"
					: "Error in saving Task data & members");

			ApiResponseDTO<SavedTaskCardDTO> apiResponse = new ApiResponseDTO<SavedTaskCardDTO>(successFlag,
					savedTaskCardDTO, msg);

			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			System.out.println("Exception while saving task-card : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving task-card data");
		}
	}

	public ResponseEntity<?> updateTaskCard(TaskDataDTO taskDataDTO) {
		System.out.println("inside TaskCardService :: updatedTaskCard, taskData: " + taskDataDTO.toString() + " \n");
		try {
			// Getting taskCard using the task-card id. which we have received from the front-end
			Optional<TaskCard> optTaskCard = taskCardRepository.findById(taskDataDTO.getId());
			if (optTaskCard.isEmpty()) {
				throw new IllegalArgumentException("TaskCard not found: id= " + taskDataDTO.getId());
			}

			TaskCard taskCard = optTaskCard.get();

			boolean taskChanged = false;

			if (!Objects.equals(taskCard.getName(), taskDataDTO.getName())) {
				taskCard.setName(taskDataDTO.getName());
				taskChanged = true;
			}

			if (!Objects.equals(taskCard.getDescription(), taskDataDTO.getDescription())) {
				taskCard.setDescription(taskDataDTO.getDescription());
				taskChanged = true;
			}

			if (taskCard.isActive() != taskDataDTO.isActive()) {
				taskCard.setActive(taskDataDTO.isActive());
				taskChanged = true;
			}

			if (taskCard.isCompleted() != taskDataDTO.isCompleted()) {
				taskCard.setCompleted(taskDataDTO.isCompleted());
				taskChanged = true;
			}

			// Most important: check if task was moved to another list
			if (!Objects.equals(taskCard.getBoardList().getId(), taskDataDTO.getListId())) {
				
				Optional<BoardList> boardListOpt = boardListRepository.findById(taskDataDTO.getListId());
				if (boardListOpt.isEmpty()) {
					throw new IllegalArgumentException("BoardList not found, while updating task-card");
				}
				BoardList boardList = boardListOpt.get();
				
				taskCard.setBoardList(boardList);
				taskChanged = true;
			}
			
			// Checking if updates are made to Task-members
			
			// getting existing members of the task
			List<SavedTaskMemberDTO> existingMembersList = taskMemberRepository.getAllTaskMembers(taskCard.getId());
			
			// mapping user-id to role for existing-members
			Map<Integer, Integer> existingMembersMap = existingMembersList.stream()
					.collect(Collectors.toMap(
							SavedTaskMemberDTO :: getUserId, 
							SavedTaskMemberDTO :: getRole
							));
			
			// getting updated-members of the task from taskDataDTO
			TaskDataDTO.Member[] updatedMembersArr = taskDataDTO.getMembers();
			
			// mapping user-id to role for updated-members
			Map<Integer, Integer> updatedMembersMap = new HashMap<>();
			for(TaskDataDTO.Member member: updatedMembersArr) {
				updatedMembersMap.put(member.getId(), member.getRole());
			}
			
			// Lists for various purposes
			List<TaskMember> addList = new ArrayList<>();
			List<TaskMember> removeList = new ArrayList<>();
			List<TaskMember> updateList = new ArrayList<>();
			
			// Checking existing-members to 
			// Detect existing-members to be removed or updated
			for (SavedTaskMemberDTO existingMember : existingMembersList) {
			    int userId = existingMember.getUserId();
			    int existingRole = existingMember.getRole();
			    
			    // creating TaskMember obj.
			    TaskMember taskMember = new TaskMember();
		    	
		    	// getting app-user
		        AppUser user = userRepository.findById(userId)
		            .orElseThrow(() -> new IllegalArgumentException("User not found"));
		        
		        // getting task-card
		        TaskCard card = taskCardRepository.findById(taskDataDTO.getId())
		            .orElseThrow(() -> new IllegalArgumentException("Task-card not found"));
		        
		        // setting task-member
		    	taskMember.setUser(user);
		    	taskMember.setTaskCard(card);
		    	taskMember.setRole(existingRole);

		        // checking in updatedMembersMap
			    if (!updatedMembersMap.containsKey(userId)) {
			        // existing-member needs to be Removed
			    	
			    	// add to removeList
			        removeList.add(taskMember);
			        
			    	taskChanged = true;
			    } else {
			    	// existingMember is present in updatedMemberMap
			    	// let's check for change in role
			    	// getting new-role
			        Integer newRole = updatedMembersMap.get(userId);
			        
			        // checking new-role & existing-role
			        if (!Objects.equals(existingRole, newRole)) {
			            // Role changed
			            taskMember.setRole(newRole);
			            
			            // add to updateList
			            updateList.add(taskMember);

				    	taskChanged = true;
			        }
			    }
			}
			
			
			// Checking new-members to 
			// Detect new-members to be added
			for (Map.Entry<Integer, Integer> mapEntry: updatedMembersMap.entrySet()) {
				int userId = mapEntry.getKey();
				int role = mapEntry.getValue();
				
				// new-member should not exist in existingMembersMap
				if (!existingMembersMap.containsKey(userId)) {
					// add the new-member to the addList
					
				    // creating TaskMember obj.
				    TaskMember taskMember = new TaskMember();
			    	
			    	// getting app-user
			        AppUser user = userRepository.findById(userId)
			            .orElseThrow(() -> new IllegalArgumentException("User not found"));
			        
			        // getting task-card
			        TaskCard card = taskCardRepository.findById(taskDataDTO.getId())
			            .orElseThrow(() -> new IllegalArgumentException("Task-card not found"));
			        
			        // setting task-member
			    	taskMember.setUser(user);
			    	taskMember.setTaskCard(card);
			    	taskMember.setRole(role);
			    	
			    	// pushing to addList
			    	addList.add(taskMember);
			    	
			    	taskChanged = true;
				}
			}
			
			// saving the updates to Db
			System.out.println("task-members to add: ");
			for(TaskMember tm: addList) {
				System.out.println(tm.toString());
			}
			taskMemberRepository.saveAll(addList);
			
			System.out.println("task-members to update: ");
			for(TaskMember tm: updateList) {
				System.out.println(tm.toString());
			}
			taskMemberRepository.saveAll(updateList);
			
			System.out.println("task-members to remove: ");
			for(TaskMember tm: removeList) {
				System.out.println(tm.toString());
			}
			taskMemberRepository.deleteAll(removeList);
			

			// checking if taskCard was updated or not
			if (taskChanged) {
				// saving the updates to the task-card
				taskCard = taskCardRepository.save(taskCard);
				System.out.println(
						"Saved updates to taskCard with id: " + 
								taskCard.getId() + " & name: " + taskCard.getName());
			} else {
				System.out.println(
						"No changes were made to taskCard with id: " + 
								taskCard.getId() + " & name: " + taskCard.getName());
			}

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String msg = "Task data & members saved successfully";

			ApiResponseDTO<TaskDataDTO> apiResponse = new ApiResponseDTO<>(successFlag,
					taskDataDTO, msg);

			return ResponseEntity.ok(apiResponse);
		} catch (IllegalArgumentException e) {
			System.out.println("Exception with data: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while updating task-card");
		} catch (Exception e) {
			System.out.println("Exception while updating task-card : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while updating task-card data");
		}
	}

	@Transactional
	public ResponseEntity<?> deleteTaskCard(Integer cardId) {
		System.out.println("Inside TaskCardService :: deleteTaskCard, cardId: " + cardId);
		try {
			taskCardRepository.deleteById(cardId);

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = true;
			String msg = "Task-card deleted successfully";

			ApiResponseDTO<Integer> apiResponse = new ApiResponseDTO<Integer>(successFlag, cardId, msg);

			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			System.out.println("Exception while deleting task-card : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while deleting task-card, invalid id");
		}
	}

}
