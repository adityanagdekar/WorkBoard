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
import ToastMsg from "./ToastMsg";

import useAuthCheck from "../token/useAuthCheck";
import capitaliseName from "../utility/capitaliseName";
import handleLogout from "../utility/handleLogout";

import "../style/ManageDashboard.css";

const ManageDashboard = () => {
  // to do auth-check -> check whether user needs to login again or not
  useAuthCheck();

  const navigate = useNavigate();

  // const [toggleModal, setModal] = useState(false);
  const [modalConfig, setModalConfig] = useState({
    isOpen: false,
    message: "",
    onYesBtnClick: () => {},
  });

  const [toggleAddBoardModal, setAddBoardModal] = useState(false);

  const [toasts, setToasts] = useState([]);

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

  const headerBtnLabels = ["Add Board", "Logout"];

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Add Board":
        showAddBoardModal();
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

  const handleBoardSaved = (updatedBoard) => {
    console.log("updatedBoard: ", updatedBoard);

    const boardIdx = boardList.findIndex(
      (board) => board.id === updatedBoard.id
    );

    setBoardList((prev) => {
      const updatedboardList = [...prev];
      if (boardIdx && boardIdx > 0) {
        // update the board details
        updatedboardList[boardIdx] = { ...updatedBoard };
      } else {
        // add the new board
        updatedboardList.push(updatedBoard);
      }
      console.log(
        "inside handleBoardSaved...updatedBoardList: ",
        updatedboardList
      );
      return updatedboardList;
    });
    closeAddBoardModal();
  };

  const openModal = ({ msg, isOpen, onYesBtnClick }) => {
    setModalConfig({
      msg: msg,
      isOpen: isOpen,
      onYesBtnClick: onYesBtnClick,
    });
  };

  const closeModal = () => {
    console.log("close the modal");
    setModalConfig((prev) => ({ ...prev, isOpen: false }));
  };

  const boardCardHeaderOnClick = (board) => {
    console.log("boardCardHeaderOnClick");
    const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

    // map to store {member-id : member-role}
    const membersMap = {};
    boardList.forEach((board) => {
      board.members.forEach((member) => {
        membersMap[member.memberId] = member.memberRole;
      });
    });

    console.log("membersMap: ", membersMap);

    navigate(`/board/${board.boardId}`, {
      state: {
        userId: loggedIn_userId,
        boardName: board.boardName,
        boardDesc: board.boardDesc,
        membersMap: membersMap,
      },
    });
  };

  const headerCloseBtnOnClick = (idx) => {
    console.log("headerCloseBtnOnClick");
    setProjectToRemoveIdx(idx);
    setModal(true);
  };

  const removeBoardOnClick = async (boardId) => {
    console.log("removeBoardOnClick, boardId: ", boardId);

    const boardIdx = boardList.findIndex((board) => board.boardId === boardId);

    if (boardIdx != null) {
      const response = await deleteBoard(boardId);

      if (response.success) {
        setBoardList((prev) => {
          const updatedboardList = [...prev];
          // Remove from source
          updatedboardList.splice(boardIdx, 1);
          return updatedboardList;
        });
        addToast("Board deleted successfully", "success");
      } else {
        addToast("Failed to delete the Board", "error");
      }
    }
  };

  const deleteBoard = async (boardId) => {
    try {
      const url = "http://localhost:8080/api/board/delete";
      const data = { id: boardId };
      const headersObj = { "Content-Type": "application/json" };
      const configObj = {
        headers: headersObj,
        withCredentials: true,
      };
      const response = await axios.post(url, data, configObj);
      const responseData = response.data;

      console.log("Response after deleting the board: ", responseData);
      return responseData;
    } catch (error) {
      console.log(
        "Failed to delete board: ",
        error.response?.data || error.message
      );
      return { success: false };
    }
  };

  const getHeaderMsg = () => {
    const name = JSON.parse(localStorage.getItem("user")).name;
    return "Hello, " + capitaliseName(name);
  };

  const getColorAsPerRole = (board) => {
    console.log("inside getColorAsPerRole");
    const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

    const memberObj = board.members.find(
      (member) => member.memberId === loggedIn_userId
    );

    return memberObj.memberRole === 1
      ? { border: "2px solid #4caf50" }
      : { border: "2px solid #667eea" };
  };

  const addToast = (message, type = "success") => {
    const currId = Date.now(); // unique key

    // Update the toasts-state
    setToasts((prev) => {
      const updatedToasts = [...prev];
      const toastMsg = {
        id: currId,
        message: message,
        type: type,
      };
      updatedToasts.push(toastMsg);
      return updatedToasts;
    });

    // Auto-remove after 3 seconds
    setTimeout(() => {
      setToasts((prev) => {
        // return all the toasts except the one which has it's id eq. to the currId
        const updatedToasts = prev.filter((toast) => toast.id !== currId);
        return updatedToasts;
      });
    }, 3000);
  };

  const removeToast = (id) => {
    // return all the toasts except the one which has it's id eq. to the currId
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
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

        <ToastMsg toasts={toasts} removeToast={removeToast} />

        <div className="BoardCardContainer">
          {boardList.length > 0 ? (
            boardList.map((board) => {
              return (
                <BoardCard key={board.boardId} style={getColorAsPerRole(board)}>
                  <BoardCardHeader
                    board={board.boardName}
                    headerOnClick={() => {
                      boardCardHeaderOnClick(board);
                    }}
                    closeBtnOnClick={
                      //headerCloseBtnOnClick
                      () => {
                        openModal({
                          msg: "Do you want delete this board ?",
                          isOpen: true,
                          onYesBtnClick: () => {
                            // sending list.id
                            removeBoardOnClick(board.boardId);
                          },
                        });
                      }
                    }
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

        {modalConfig.isOpen && (
          <Modal
            modalMsg={"Do you want to remove this Project ?"}
            modalYesOnClick={() => {
              modalConfig.onYesBtnClick();
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
            closeBtnOnClick={closeAddBoardModal}
            onBackDropClick={closeAddBoardModal}
            addToast={addToast}
            removeToast={removeToast}
            onBoardSave={handleBoardSaved}
          />
        )}
      </div>
    </BoardContainer>
  );
};
export default ManageDashboard;
