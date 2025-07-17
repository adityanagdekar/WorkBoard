import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import BoardContainer from "./BoardContainer";
import WorkBoardHeader from "./MainHeader";
import BoardCard from "./BoardCard";
import BoardCardHeader from "./BoardCardHeader";
import BoardHeader from "./BoardHeader";
import Modal from "./Modal";
import AddBoardModal from "./AddBoardModal";
import useAuthCheck from "../token/useAuthCheck";
import capitaliseName from "../utility/capitaliseName";
import handleLogout from "../utility/handleLogout";

import "../style/ManageDashboard.css";

const ManageDashboard = () => {
  // to do auth-check -> check whether user needs to login again or not
  useAuthCheck();

  const navigate = useNavigate();

  const [toggleModal, setModal] = useState(false);
  const [toggleAddBoardModal, setAddBoardModal] = useState(false);

  const [boardList, setBoardList] = useState([]);

  const [projectToRemoveIdx, setProjectToRemoveIdx] = useState(null);

  useEffect(() => {
    const getBoards = async () => {
      const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;
      try {
        const url = "http://localhost:8080/api/board/boards";
        // const paramsObj = {
        //   userId: loggedIn_userId,
        // };
        const configObj = {
          // params: paramsObj,
          withCredentials: true,
        };
        const response = await axios.get(url, configObj);

        const boardsData = response.data.data;
        console.log("fetched board-data: ", boardsData);

        setBoardList(boardsData);
      } catch (error) {
        console.log("Failed to get boards: ", error);
      }
    };
    getBoards();
  }, []);

  const headerBtnLabels = [
    "Add Board",
    "Add Members",
    "Assign Roles",
    "Logout",
  ];

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Add Board":
        showAddBoardModal();
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
        handleLogout(navigate);
        break;
      default:
        console.warn("Unknown header button clicked");
    }
  };

  const showAddBoardModal = () => {
    console.log("show Project modal");
    setAddBoardModal((prevState) => prevState || true);
  };

  const closeAddBoardModal = () => {
    console.log("close Project modal");
    setAddBoardModal((prevState) => prevState && false);
  };

  const closeModal = () => {
    setModal((prevState) => prevState && false);
  };

  const saveBoardModelData = () => {};

  const boardCardHeaderOnClick = (board) => {
    console.log("boardCardHeaderOnClick");
    navigate(`/board/${board.boardId}`, {
      state: {
        userId: board.members[0].memberId,
      },
    });
  };

  const headerCloseBtnOnClick = (idx) => {
    console.log("headerCloseBtnOnClick");
    setProjectToRemoveIdx(idx);
    setModal(true);
  };

  const removeProjectOnClick = (index) => {
    console.log("removeProjectOnClick index: ", index);
    if (index != null) {
      setBoardList((prev) => {
        const updatedboardList = [...prev];
        // Remove from source
        updatedboardList.splice(index, 1);
        return updatedboardList;
      });
    }
  };

  const getHeaderMsg = () => {
    const name = JSON.parse(localStorage.getItem("user")).name;
    return "Hello, " + capitaliseName(name);
  };

  return (
    <BoardContainer>
      <div className="Dashboard-Container">
        <WorkBoardHeader message="Workboard" />

        <BoardHeader
          headerMsg={getHeaderMsg()}
          btnLabels={headerBtnLabels}
          headerBtnOnClick={handleHeaderBtnClick}
        />

        <div className="BoardCardContainer">
          {boardList.length > 0 ? (
            boardList.map((board) => {
              return (
                <BoardCard
                  key={board.boardId}
                  style={
                    board.members[0].memberRole === 1 // highlighting border based on memberRole
                      ? { border: "2px solid #4caf50" }
                      : { border: "2px solid #667eea" }
                  }
                >
                  <BoardCardHeader
                    board={board.boardName}
                    headerOnClick={() => {
                      boardCardHeaderOnClick(board);
                    }}
                    closeBtnOnClick={headerCloseBtnOnClick}
                  />
                  <div className="BoardCardContent">
                    <p>Description: {board.boardDesc}</p>
                  </div>
                </BoardCard>
              );
            })
          ) : (
            <p> Currently, there are no boards but you can create yours !!</p>
          )}
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

        {toggleAddBoardModal && (
          <AddBoardModal
            saveBtnOnClick={saveBoardModelData}
            closeBtnOnClick={closeAddBoardModal}
            onBackDropClick={closeAddBoardModal}
          />
        )}
      </div>
    </BoardContainer>
  );
};
export default ManageDashboard;
