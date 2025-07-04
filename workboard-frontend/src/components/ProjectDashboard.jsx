import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import BoardContainer from "./BoardContainer";
import WorkBoardHeader from "./MainHeader";
import BoardCard from "./BoardCard";
import BoardCardHeader from "./BoardCardHeader";
import BoardHeader from "./BoardHeader";
import Modal from "./Modal";
import AddProjectModal from "./AddProjectModal";

import "../style/ProjectDashboard.css";

const ProjectDashboard = () => {
  const navigate = useNavigate();

  const [toggleModal, setModal] = useState(false);
  const [toggleAddProjectModal, setAddProjectModal] = useState(false);

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

  const headerBtnLabels = [
    "Add Project",
    "Add Members",
    "Assign Roles",
    "Logout",
  ];

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Add Project":
        showAddProjectModal();
        break;
      case "Add Members":
        console.log("Add Members clicked");
        // addMembersOnClick();
        break;
      case "Assign Roles":
        console.log("Assign Roles clicked");
        // assignRolesOnClick();
        break;
      case "Logout":
        console.log("Logout btn clicked");
        handleLogout();
        break;
      default:
        console.warn("Unknown header button clicked");
    }
  };

  const handleLogout = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/user/logout",
        {},
        {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      navigate("/");
    } catch (error) {
      console.error("Logout failed", error);
    }
  };

  const showAddProjectModal = () => {
    console.log("show Project modal");
    setAddProjectModal((prevState) => prevState || true);
  };

  const closeAddProjectModal = () => {
    console.log("close Project modal");
    setAddProjectModal((prevState) => prevState && false);
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
      setProjectList((prev) => {
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
          headerBtnOnClick={handleHeaderBtnClick}
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
              removeProjectOnClick(projectToRemoveIdx);
              closeModal();
            }}
            modalNoOnClick={() => {
              closeModal();
            }}
            onBackdropClick={() => closeModal()}
          />
        )}

        {toggleAddProjectModal && (
          <AddProjectModal
            doneBtnOnClick={closeAddProjectModal}
            onBackDropClick={closeAddProjectModal}
          />
        )}
      </div>
    </BoardContainer>
  );
};
export default ProjectDashboard;
