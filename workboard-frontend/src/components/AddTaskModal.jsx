import { useEffect, useState } from "react";
import axios from "axios";

import "../style/Modal.css";
import "../style/AddBoardModal.css";

import BoardBtn from "./BoardBtn";
import ToastMsg from "./ToastMsg";
import capitaliseName from "../utility/capitaliseName";

const AddTaskModal = ({
  closeBtnOnClick,
  onBackDropClick,
  boardId,
  listIdx,
  listId,
  cardObj,
  addToast,
  closeToast,
  removeDummyTaskCard,
  addTaskToState,
}) => {
  const [boardMembers, setBoardMembers] = useState([]);
  const [taskMembers, setTaskMembers] = useState([]);
  const [taskName, setTaskName] = useState(cardObj ? cardObj.name : "");
  const [taskDesc, setTaskDesc] = useState(cardObj ? cardObj.desc : "");
  const [isCompleted, setIsCompleted] = useState(
    cardObj ? cardObj.isCompleted : false
  );
  const [isActive, setIsActive] = useState(cardObj ? cardObj.isActive : false);

  // To fetch all the board-members
  useEffect(() => {
    if (boardId) {
      const getBoardMembers = async (boardId) => {
        try {
          const url = `http://localhost:8080/api/board/members/${boardId}`;
          const configObj = {
            withCredentials: true,
          };
          const response = await axios.get(url, configObj);
          console.log("board-members fetched data: ", response.data);

          const members = response.data.data;

          const initialTaskMembers = members.map((member) => {
            const userObj = {
              id: member.id,
              name: member.name,
              isAdded: false, // this tells us whether member is added to the task or not
              role: "", // this tels us the role of the member
            };

            // checking if the member is board-creator based on id
            const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

            userObj.role = member.id === loggedIn_userId ? 1 : 0; // role set as MANAGER i.e. 1
            userObj.isAdded = member.id === loggedIn_userId ? true : false; // isAdded attr. set as true
            return userObj;
          });

          console.log("initialTaskMembers: ", initialTaskMembers);

          setBoardMembers(members);
          setTaskMembers(initialTaskMembers);
        } catch (error) {
          console.log("Failed to get board-members: ", error);
        }
      };
      getBoardMembers(boardId);
    }
  }, [boardId]);

  const handleAssignRole = (e, user) => {
    console.log("Role dropdown altered");
    const selectedRole = e.target.value;
    // console.log("boardMembers: ", boardMembers);
    setTaskMembers((prevMembers) =>
      prevMembers.map((member) =>
        member.id === user.id ? { ...member, role: selectedRole } : member
      )
    );
  };

  const handleAddMemberChkBox = (e, user) => {
    console.log("add member chkbox clicked");
    console.log("user: ", user);
    const isChecked = e.target.checked;
    // console.log("boardMembers: ", boardMembers);
    setTaskMembers((prevMembers) => {
      const updatedMembers = prevMembers.map((member) =>
        member.id === user.id ? { ...member, isAdded: isChecked } : member
      );
      console.log(updatedMembers);
      return updatedMembers;
    });
  };

  const checkIsMember = (user) => {
    // console.log("boardMembers: ", boardMembers);
    const member = taskMembers.find((member) => member.id === user.id);
    if (member && member.hasOwnProperty("isAdded")) return member.isAdded;
    return false;
  };

  const checkRole = (user) => {
    // console.log("boardMembers: ", boardMembers);
    const member = taskMembers.find((member) => member.id === user.id);
    if (member && member.hasOwnProperty("role")) return member.role;
    return false;
  };

  const handleTaskNameChange = (name) => {
    console.log("task name changed: ", name);
    setTaskName((prev) => name);
  };

  const handleTaskDescChange = (desc) => {
    console.log("task desc. changed: ", desc);
    setTaskDesc((prev) => desc);
  };

  /*
    {
    "success": true,
    "data": {
        "taskId": 11,
        "members": [
            {
                "memberId": 1,
                "memberRole": 0
            },
            {
                "memberId": 10,
                "memberRole": 1
            }
        ],
        "name": "Create dashboard for reserved items",
        "desc": "Create dashboard for reserved items"
    },
    "message": "Task data & members saved successfully"
}
    */

  const saveTaskBtnOnClick = () => {
    console.log("save btn clicked");
    const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

    const selectedMembers = taskMembers.filter(
      (member) => member.isAdded && member.role >= 0
    );
    const taskData = {
      name: taskName,
      description: taskDesc,
      is_active: isActive,
      is_completed: isCompleted,
      members: selectedMembers,
      userId: loggedIn_userId,
      listId: listId,
    };

    console.log("task-data to be saved: ", taskData);
    saveTaskData(taskData)
      .then((response) => {
        console.log("response in saveTaskBtnOnClick(): ", response);
        console.log("response.success: ", response.success);

        if (response.success === true) {
          addToast("Task-card added successfully", "success");

          // setting up newCard using data sent from backend
          const taskDataFetched = response.data;
          const newCard = {
            name: taskDataFetched.name,
            desc: taskDataFetched.desc,
            isActive: taskDataFetched.isActive,
            isCompleted: taskDataFetched.isCompleted,
          };
          // removing dummyCard from the cards[] from boardList having idx === listIdx
          removeDummyTaskCard(listIdx);

          // adding newCard to state to render it
          addTaskToState(listIdx, newCard);
        } else addToast("Failed to add the Task-card", "error");
      })
      .catch((error) => {
        console.log(
          "Saving Task-data failed:",
          error.response?.data || error.message
        );
        addToast("Failed to add the Task-card", "error");
      });
  };

  const saveTaskData = async (taskData) => {
    try {
      const url = "http://localhost:8080/api/task/save";
      const data = taskData;
      const configObj = {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      };
      return axios
        .post(url, data, configObj)
        .then((response) => {
          console.log("Task-data saved successfully: ", response.data);
          return response.data;
        })
        .catch((error) => {
          console.log(
            "Saving Task-data failed:",
            error.response?.data || error.message
          );
          return { success: false };
        });
    } catch (error) {
      console.log(
        "Saving Task-data failed:",
        error.response?.data || error.message
      );
      return { success: false };
    }
  };

  return (
    <div className="ModalOverlay" onClick={onBackDropClick}>
      <div className="Modal-AddProject" onClick={(e) => e.stopPropagation()}>
        <div className="ModalContent-Project">
          {/* Project info section */}

          <div className="InfoSection">
            <label>Name</label>
            <input
              type="text"
              required
              value={taskName}
              onChange={(e) => {
                handleTaskNameChange(e.target.value);
              }}
            />
            <label>Description</label>
            <textarea
              type="text"
              required
              value={taskDesc}
              onChange={(e) => {
                handleTaskDescChange(e.target.value);
              }}
            ></textarea>

            <label>Is the task completed ?</label>
            <div style={{ display: "flex", gap: "1rem", marginBottom: "1rem" }}>
              <label>
                <input
                  type="radio"
                  name="isCompleted"
                  value="true"
                  checked={isCompleted === true}
                  onChange={() => setIsCompleted(true)}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="isCompleted"
                  value="false"
                  checked={isCompleted === false}
                  onChange={() => setIsCompleted(false)}
                />
                No
              </label>
            </div>

            {/* Radio group for isActive */}
            <label>Is the task still active ?</label>
            <div style={{ display: "flex", gap: "1rem" }}>
              <label>
                <input
                  type="radio"
                  name="isActive"
                  value="true"
                  checked={isActive === true}
                  onChange={() => setIsActive(true)}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="isActive"
                  value="false"
                  checked={isActive === false}
                  onChange={() => setIsActive(false)}
                />
                No
              </label>
            </div>
          </div>

          {/* Project members table section */}
          <div className="MemberSection">
            <label>Select Members who will work on this task</label>
            <input placeholder="Search Members"></input>
            <table className="UserTable">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Add to Task ?</th>
                  <th>Assign Role</th>
                </tr>
              </thead>
              <tbody>
                {taskMembers.map((taskMember, idx) => {
                  const loggedIn_userId = JSON.parse(
                    localStorage.getItem("user")
                  ).id;
                  return (
                    <tr key={idx}>
                      <td>{capitaliseName(taskMember.name)}</td>
                      <td>
                        <input
                          type="checkbox"
                          checked={checkIsMember(taskMember)}
                          onChange={(e) => handleAddMemberChkBox(e, taskMember)}
                          /* If the user is board-creator/logged-in user then by-default 
                            mark this checkbox & disable it */
                          disabled={
                            taskMember.id === loggedIn_userId ? true : false
                          }
                        />
                      </td>
                      <td>
                        <select
                          value={checkRole(taskMember)}
                          onChange={(e) => handleAssignRole(e, taskMember)}
                          /* If the user is board-creator/logged-in user then by-default 
                            set the role as Manager & disable it*/
                          disabled={
                            taskMember.id === loggedIn_userId ? true : false
                          }
                        >
                          <option value="">-- select --</option>
                          {/* 
                          if role is Manager it means 1 at the backend
                          and if it Member then it means 0
                        */}
                          <option value="1">Manager</option>
                          <option value="0">Member</option>
                        </select>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {/* Footer section */}
        <div className="ModalFooter">
          <div className="ModalBtnContainer">
            <BoardBtn
              label="Save"
              variant="modal-yes"
              onClick={() => {
                saveTaskBtnOnClick();
                closeBtnOnClick();
              }}
            />
            <BoardBtn
              label="Close"
              variant="modal-no"
              onClick={closeBtnOnClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
export default AddTaskModal;
