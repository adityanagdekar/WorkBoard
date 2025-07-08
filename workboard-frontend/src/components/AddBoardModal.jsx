import Select from "react-select";
import { useEffect, useState } from "react";
import axios from "axios";

import "../style/Modal.css";
import "../style/AddProjectModal.css";

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

const AddProjectModal = ({ doneBtnOnClick, onBackDropClick }) => {
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      try {
        const url = "http://localhost:8080/api/user/all";
        const configObj = { withCredentials: true };
        const response = await axios.get(url, configObj);

        setUsers(response.data);
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

  const handleAssignRole = (id, role) => {
    console.log(`user with id:${id} has been assigned this role: ${role}`);
  };

  const addMemberBtnStyle = { width: "20%", height: "30%" };

  return (
    <div className="ModalOverlay" onClick={onBackDropClick}>
      <div className="Modal-AddProject" onClick={(e) => e.stopPropagation()}>
        <div className="ModalContent-Project">
          {/* Project info section */}
          <div className="InfoSection">
            <label>Name</label>
            <input type="text" required />
            <label>Description</label>
            <input type="text" required />
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
                      <input type="checkbox" />
                    </td>
                    <td>
                      <select
                        onChange={(e) =>
                          handleAssignRole(user.id, e.target.value)
                        }
                        defaultValue=""
                      >
                        <option disabled value="">
                          -- select --
                        </option>
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
              onClick={() => {
                console.log("Selected members:", selectedMembers);
                doneBtnOnClick();
              }}
            />
            <BoardBtn
              label="Close"
              variant="modal-no"
              onClick={doneBtnOnClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
export default AddProjectModal;

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
