import { useEffect, useState } from "react";
import axios from "axios";

import "../style/Modal.css";
import "../style/AddBoardModal.css";

import BoardBtn from "./BoardBtn";
import ToastMsg from "./ToastMsg";

import capitaliseName from "../utility/capitaliseName";

const AddBoardModal = ({ closeBtnOnClick, onBackDropClick }) => {
  const [boardName, setBoardName] = useState("");
  const [description, setDescription] = useState("");

  const [boardMembers, setBoardMembers] = useState([]);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      try {
        const url = "http://localhost:8080/api/user/users";
        const configObj = { withCredentials: true };
        const response = await axios.get(url, configObj);

        const boardMembersData = response.data.data;

        const initialBoardMembers = boardMembersData.map((user) => {
          const userObj = {
            id: user.id,
            name: user.name,
            isAdded: false, // this tells us whether user is added to the board or not
            role: "", // this tels us the role of the user
          };

          // checking if the user is board-creator based on id
          const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

          userObj.role = user.id === loggedIn_userId ? 1 : 0; // role set as MANAGER i.e. 1
          userObj.isAdded = user.id === loggedIn_userId ? true : false; // isAdded attr. set as true
          return userObj;
        });

        console.log("initialBoardMembers: ");
        console.log(initialBoardMembers);

        setUsers(boardMembersData);
        setBoardMembers(initialBoardMembers);
      } catch (err) {
        console.log("Failed to get users: ", err);
      }
    };
    getUsers();
  }, []);

  // Example options (you can fetch or pass as props)
  const memberOptions = [
    { value: "alice", label: "Alice" },
    { value: "bob", label: "Bob" },
    { value: "charlie", label: "Charlie" },
  ];

  const roleOptions = [
    { value: "manager", label: "Manager" },
    { value: "member", label: "Member" },
  ];

  // const users = [{ id: "1", name: "John" }];

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

  const saveBoardData = async (boardData) => {
    try {
      const url = "http://localhost:8080/api/board/save";
      const data = boardData;
      const configObj = {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      };
      const response = await axios.post(url, data, configObj);
      console.log("board saved successfully: ", response.data);
    } catch (error) {
      console.error(
        "Saving Board failed:",
        error.response?.data || error.message
      );
    }
  };

  const saveBtnOnClick = () => {
    const selectedMembers = boardMembers.filter(
      (member) => member.isAdded && member.role >= 0
    );

    // loggedIn_user -> means board-creator or manager
    const loggedIn_user = JSON.parse(localStorage.getItem("user"));
    const rawBoardId = localStorage.getItem("boardId");
    const parsedBoardId = rawBoardId ? parseInt(rawBoardId, 10) : NaN;

    const savedBoardId = Number.isNaN(parsedBoardId) ? -1 : parsedBoardId;
    console.log("selectMembers: ", selectedMembers);
    const boardData = {
      boardId: savedBoardId,
      name: boardName,
      description: description,
      members: selectedMembers,
      userId: loggedIn_user.id,
    };
    console.log("Final board data:", boardData);
    saveBoardData(boardData);
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
              value={boardName}
              onChange={(e) => {
                setBoardName(e.target.value);
              }}
            />
            <label>Description</label>
            <input
              type="text"
              required
              value={description}
              onChange={(e) => {
                setDescription(e.target.value);
              }}
            />
          </div>

          {/* Project members table section */}
          <div className="MemberSection">
            <input placeholder="Search Members"></input>
            <table className="UserTable">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Add to project ?</th>
                  <th>Assign Role</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => {
                  const loggedIn_userId = JSON.parse(
                    localStorage.getItem("user")
                  ).id;
                  return (
                    <tr key={user.id}>
                      <td>{capitaliseName(user.name)}</td>
                      <td>
                        <input
                          type="checkbox"
                          checked={checkIsMember(user)}
                          onChange={(e) => handleAddMemberChkBox(e, user)}
                          /* If the user is board-creator/logged-in user then by-default 
                            mark this checkbox & disable it */
                          disabled={user.id === loggedIn_userId ? true : false}
                        />
                      </td>
                      <td>
                        <select
                          value={checkRole(user)}
                          onChange={(e) => handleAssignRole(e, user)}
                          /* If the user is board-creator/logged-in user then by-default 
                            set the role as Manager & disable it*/
                          disabled={user.id === loggedIn_userId ? true : false}
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
              onClick={saveBtnOnClick}
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
export default AddBoardModal;

{
  /* <div className="DropdownContainer">
              <div className="SelectContainer">
                <p>Search and select Members</p>
                <Select
                  styles={selectDropdownStyles}
                  options={memberOptions}
                  onChange={setSelectedMembers}
                  value={selectedMembers}
                  placeholder=""
                />
              </div>
              <div className="SelectContainer">
                <p>Search and select Roles</p>
                <Select
                  styles={selectDropdownStyles}
                  options={roleOptions}
                  onChange={setSelectedRoles}
                  value={selectedRoles}
                  placeholder=""
                />
              </div>
            </div> 
             {<BoardBtn
              label="Add Members"
              onClick={() => {
                console.log("New member to add");
              }}
            />}  */
}
