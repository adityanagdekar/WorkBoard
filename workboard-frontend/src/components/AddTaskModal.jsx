import { useEffect, useState } from "react";
import axios from "axios";

import "../style/Modal.css";
import "../style/AddBoardModal.css";

import BoardBtn from "./BoardBtn";
import capitaliseName from "../utility/capitaliseName";

const AddTaskModal = ({ closeBtnOnClick, onBackDropClick }) => {
  const [boardMembers, setBoardMembers] = useState([
    {
      id: 1,
      name: "John Doe",
      isAdded: true,
      role: 1,
    },
    {
      id: 8,
      name: "zack jones",
      isAdded: false,
      role: 0,
    },
    {
      id: 9,
      name: "tim brooks",
      isAdded: false,
      role: 0,
    },
    {
      id: 10,
      name: "Shree",
      isAdded: false,
      role: 0,
    },
    {
      id: 11,
      name: "Niel",
      isAdded: false,
      role: 0,
    },
  ]);

  const [taskName, setTaskName] = useState("");
  const [taskDesc, setTaskDesc] = useState("");
  const [isCompleted, setIsCompleted] = useState(false);
  const [isActive, setIsActive] = useState(true);

  const handleAssignRole = (e, user) => {
    console.log("Role dropdown altered");
    const selectedRole = e.target.value;
    // console.log("boardMembers: ", boardMembers);
    setBoardMembers((prevMembers) =>
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
    setBoardMembers((prevMembers) => {
      const updatedMembers = prevMembers.map((member) =>
        member.id === user.id ? { ...member, isAdded: isChecked } : member
      );
      console.log(updatedMembers);
      return updatedMembers;
    });
  };

  const checkIsMember = (user) => {
    // console.log("boardMembers: ", boardMembers);
    const member = boardMembers.find((member) => member.id === user.id);
    if (member && member.hasOwnProperty("isAdded")) return member.isAdded;
    return false;
  };

  const checkRole = (user) => {
    // console.log("boardMembers: ", boardMembers);
    const member = boardMembers.find((member) => member.id === user.id);
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

  const saveTaskBtn = () => {
    const taskData = {
      name: taskName,
      description: taskDesc,
      is_active: isActive,
      is_completed: isCompleted,
    };
    console.log("save btn clicked");
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
              value={""}
              onChange={(e) => {
                handleTaskNameChange(e.target.value);
              }}
            />
            <label>Description</label>
            <textarea
              type="text"
              required
              value={""}
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
                </tr>
              </thead>
              <tbody>
                {boardMembers.map((boardMember) => {
                  const loggedIn_userId = JSON.parse(
                    localStorage.getItem("user")
                  ).id;
                  return (
                    <tr key={boardMember.id}>
                      <td>{capitaliseName(boardMember.name)}</td>
                      <td>
                        <input
                          type="checkbox"
                          checked={checkIsMember(boardMember)}
                          onChange={(e) =>
                            handleAddMemberChkBox(e, boardMember)
                          }
                          /* If the user is board-creator/logged-in user then by-default 
                            mark this checkbox & disable it */
                          disabled={
                            boardMember.id === loggedIn_userId ? true : false
                          }
                        />
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
            <BoardBtn label="Save" variant="modal-yes" onClick={saveTaskBtn} />
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
