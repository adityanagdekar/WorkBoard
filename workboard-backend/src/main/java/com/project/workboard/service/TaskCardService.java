package com.project.workboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.project.workboard.dto.ApiResponseDTO;
import com.project.workboard.dto.SavedTaskCardDTO;
import com.project.workboard.dto.TaskDataDTO;
import com.project.workboard.dto.SavedTaskCardDTO.TaskMemberData;
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
		System.out.println("Inside TaskCardService :: saveTaskCard");

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
				savedTaskCardDTO.setTaskId(taskCardId);
				savedTaskCardDTO.setName(savedTaskCard.getName());
				savedTaskCardDTO.setDesc(savedTaskCard.getDescription());

				// this means taskCard is saved successfully, let's save the task-members as
				// well
				try {
					// Getting task-members
					Member[] taskMembers = taskData.getMembers();
					int numTaskMembers = taskMembers.length;

					// this list. is later stored in our savedTaskCardDTO obj.
					List<TaskMemberData> taskMemberDataList = new ArrayList<>();

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
						System.out.println("successfully saved task-member with id: " + taskMemberId);

						// Saving member-data-dto in memberIds arr.
						taskMemberDataList.add(new 
								TaskMemberData(taskMemberId, 
										fetchedTaskMember.getRole())
								);

					}

					// once we have processed all task-members, save the arr. of task-members
					savedTaskCardDTO.setMembers(taskMemberDataList.toArray(new TaskMemberData[0]));

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
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving task-card");
			}

			// Data is saved successfully, let's send Api-Response for the same
			boolean successFlag = (taskCardId > 0 ? true : false);
			String msg = (taskCardId > 0 ? "Task data & members saved successfully"
					: "Error in saving Task data & members");

			ApiResponseDTO<SavedTaskCardDTO> apiResponse = new 
					ApiResponseDTO<SavedTaskCardDTO>(successFlag, savedTaskCardDTO, msg);

			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			System.out.println("Exception while saving task-card : " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error while saving task-card data");
		}
	}

}
