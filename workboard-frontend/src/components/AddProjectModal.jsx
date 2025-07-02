import Select from "react-select";
import { useState } from "react";

import "../style/Modal.css";
import BoardBtn from "./BoardBtn";

const AddProjectModal = ({ doneBtnOnClick, onBackDropClick }) => {
  const [selectedMembers, setSelectedMembers] = useState([]);

  // Example options (you can fetch or pass as props)
  const memberOptions = [
    { value: "alice", label: "Alice" },
    { value: "bob", label: "Bob" },
    { value: "charlie", label: "Charlie" },
  ];

  return (
    <div className="ModalOverlay" onClick={onBackDropClick}>
      <div className="Modal" onClick={(e) => e.stopPropagation()}>
        <div className="ModalContent-Project">
          <label>Name</label>
          <input type="text" required />
          <label>Description</label>
          <input type="text" required />
          <label>Members</label>
          <Select
            isMulti
            options={memberOptions}
            onChange={setSelectedMembers}
            value={selectedMembers}
            placeholder="Search and select members"
          />
        </div>
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
