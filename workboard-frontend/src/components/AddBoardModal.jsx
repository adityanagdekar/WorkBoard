import Select from "react-select";
import { useEffect, useState } from "react";
import axios from "axios";

import "../style/Modal.css";
import "../style/AddBoardModal.css";

import BoardBtn from "./BoardBtn";

const selectDropdownStyles = {
  control: (provided) => ({
    ...provided,
    minHeight: "10%", // height of the input box
    height: "2rem",
    width: "100%", // width of the input box
  }),
  menu: (provided) => ({
    ...provided,
    width: "100%", // width of dropdown menu
  }),
  valueContainer: (provided) => ({
    ...provided,
    height: "100%",
    padding: "0 6px",
  }),
  input: (provided) => ({
    ...provided,
    margin: "0px",
  }),
  indicatorsContainer: (provided) => ({
    ...provided,
    height: "100%",
  }),
};

const AddBoardModal = ({ closeBtnOnClick, onBackDropClick }) => {
  const [boardName, setBoardName] = useState("");
  const [description, setDescription] = useState("");

  const [boardMembers, setBoardMembers] = useState([]);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      try {
        const url = "http://localhost:8080/api/user/all";
        const configObj = { withCredentials: true };
        const response = await axios.get(url, configObj);

        const initialBoardMembers = response.data.map((user) => ({
          id: user.id,
          name: user.name,
          added: false, // later we need to change the "added" property &
          // check in db whether user
          // is associated with this board or not
          role: "",
        }));

        console.log("initialBoardMembers: ");
        console.log(initialBoardMembers);

        setUsers(response.data);
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
    const isChecked = e.target.checked;
    // console.log("boardMembers: ", boardMembers);
    setBoardMembers((prevMembers) =>
      prevMembers.map((member) =>
        member.id === user.id ? { ...member, added: isChecked } : member
      )
    );
  };

  const checkIsMember = (user) => {
    console.log("boardMembers: ", boardMembers);
    const member = boardMembers.find((member) => member.id === user.id);
    if (member && member.hasOwnProperty("added")) return member.added;
    return false;
  };

  const checkRole = (user) => {
    console.log("boardMembers: ", boardMembers);
    const member = boardMembers.find((member) => member.id === user.id);
    if (member && member.hasOwnProperty("role")) return member.role;
    return false;
  };

  const saveBoardData = async (boardData) => {
    try {
      const data = boardData;
      // const configObj = {
      //   withCredentials: true,
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      // };
      // const response = await axios.post(
      //   "http://localhost:8080//api/board/save",
      //   data,
      //   configObj
      // );
      // console.log("board saved successfully: ", response.data);
    } catch (error) {
      console.error(
        "Saving Board failed:",
        error.response?.data || error.message
      );
    }
  };

  const saveBtnOnClick = () => {
    const selectedMembers = boardMembers.filter(
      (member) => member.added && member.role
    );
    const boardData = {
      name: boardName,
      description: description,
      members: selectedMembers,
    };
    console.log("Final board data:", boardData);
    saveBoardData(boardData);
  };

  const addMemberBtnStyle = { width: "20%", height: "30%" };

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
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.name}</td>
                    <td>
                      <input
                        type="checkbox"
                        checked={checkIsMember(user)}
                        onChange={(e) => handleAddMemberChkBox(e, user)}
                      />
                    </td>
                    <td>
                      <select
                        value={checkRole(user)}
                        onChange={(e) => handleAssignRole(e, user)}
                      >
                        <option value="">-- select --</option>
                        <option value="Manager">Manager</option>
                        <option value="Member">Member</option>
                      </select>
                    </td>
                  </tr>
                ))}
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
