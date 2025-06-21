import { useState } from "react";
import { useNavigate } from "react-router-dom";
import BoardContainer from "./BoardContainer";
import WorkBoardHeader from "./MainHeader";
import BoardCard from "./BoardCard";
import BoardCardHeader from "./BoardCardHeader";
import BoardHeader from "./BoardHeader";
import Modal from "./Modal";
import "../style/ProjectDashboard.css";

const ProjectDashboard = () => {
  const navigate = useNavigate();

  const [toggleModal, setModal] = useState(false);

  const [projectList, setProjectList] = useState([
    {
      name: "Project 1",
      description: "Neo School Learning Management System",
      Tasks: [
        { task: "To-Do", count: "2" },
        { task: "WIP", count: "3" },
      ],
    },
    {
      name: "Project 2",
      description: "CAT exam dashboard",
      Tasks: [
        { task: "To-Do", count: "2" },
        { task: "WIP", count: "3" },
      ],
    },

    {
      name: "Project 3",
      description: "Sunshine Healthcare",
      Tasks: [
        { task: "To-Do", count: "2" },
        { task: "WIP", count: "3" },
      ],
    },
  ]);

  const [projectToRemoveIdx, setProjectToRemoveIdx] = useState(null);

  const headerBtnLabels = ["Add Project", "Add Members", "Assign Roles"];

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Add Project":
        console.log("Add Project clicked");
        break;
      case "Add Members":
        console.log("Add Members clicked");
        // addMembersOnClick(); // Implement when ready
        break;
      case "Assign Roles":
        console.log("Assign Roles clicked");
        // assignRolesOnClick(); // Implement when ready
        break;
      default:
        console.warn("Unknown header button clicked");
    }
  };

  const boardCardHeaderOnClick = () => {
    console.log("boardCardHeaderOnClick");
    navigate("/board");
  };

  const headerCloseBtnOnClick = (idx) => {
    console.log("headerCloseBtnOnClick");
    setProjectToRemoveIdx(idx);
    setModal(true);
  };

  const closeModal = () => {
    setModal(false);
  };

  const removeProjectOnClick = (index) => {
    console.log("removeProjectOnClick index: ", index);
    if (index != null) {
      setDataList((prev) => {
        const updatedProjectList = [...prev];
        // Remove from source
        updatedProjectList.splice(index, 1);
        return updatedProjectList;
      });
    }
  };

  return (
    <BoardContainer>
      <div className="Dashboard-Container">
        <WorkBoardHeader message="Workboard" />

        <BoardHeader
          projectName={"Projects"}
          btnLabels={headerBtnLabels}
          onClick={handleHeaderBtnClick}
        />

        <div className="BoardCardContainer">
          {projectList.map((project, idx) => {
            return (
              <BoardCard key={idx}>
                <BoardCardHeader
                  project={project.name}
                  headerOnClick={boardCardHeaderOnClick}
                  closeBtnOnClick={headerCloseBtnOnClick}
                />
                <div className="BoardCardContent">
                  <p>Description: {project.description}</p>
                </div>
              </BoardCard>
            );
          })}
        </div>

        {toggleModal && (
          <Modal
            modalMsg={"Do you want to remove this Project ?"}
            modalYesOnClick={() => {
              removeProjectOnClick(phaseToRemoveIdx);
              closeModal();
            }}
            modalNoOnClick={() => {
              closeModal();
            }}
            onBackdropClick={() => closeModal()}
          />
        )}
      </div>
    </BoardContainer>
  );
};
export default ProjectDashboard;
