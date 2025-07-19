import { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import axios from "axios";

import BoardBtn from "./BoardBtn";
import TaskCard from "./TaskCard";
import Modal from "./Modal";
import BoardContainer from "./BoardContainer";
import BoardHeader from "./BoardHeader";
import MainHeader from "./MainHeader";
import useAuthCheck from "../token/useAuthCheck";
import handleLogout from "../utility/handleLogout";

import "../style/WorkBoard.css";

const WorkBoard = () => {
  // to do auth-check -> check whether user needs to login again or not
  useAuthCheck();
  const navigate = useNavigate();

  const priorities = ["P1", "P2"];
  const phases = ["To-Do", "WIP", "Test", "Review", "Deploy"];
  const headerBtnLabels = [
    "Projects",
    "Add List",
    "Add Members",
    "Assign Roles",
    "Logout",
  ];

  const taskExists = (phase) => {
    if (["To-Do", "WIP", "Test"].includes(phase)) return true;
    return false;
  };

  const [dataList, setDataList] = useState([
    {
      listName: "To-Do",
      cards: [{ name: "card 1", description: "This is the card 1" }],
    },
    {
      listName: "WIP",
      cards: [{ name: "card 2", description: "This is the card 2" }],
    },
  ]);

  const [toggleModal, setModal] = useState(false);

  const [phaseToRemoveIdx, setPhaseToRemoveIdx] = useState(null);

  // getting state-params passed
  // from ManageBoard.jsx to BoardGrid.jsx
  const { boardId } = useParams();
  const location = useLocation();
  const userId = location.state?.userId;

  useEffect(() => {
    if (boardId && userId) {
      const getBoardData = async (boardId) => {
        try {
          const url = `http://localhost:8080/api/list/lists/${boardId}`;
          const configObj = {
            withCredentials: true,
          };
          const response = await axios.get(url, configObj);

          console.log(response.data);
        } catch (error) {
          console.log("Failed to get board-data: ", error);
        }
      };
      getBoardData(boardId);
    }
  }, [boardId, userId]);

  const openModalForPhase = (index) => {
    console.log("openModalForPhase with index: ", index);
    setPhaseToRemoveIdx(index);
    setModal(true);
  };

  const closeModal = () => {
    console.log("close the modal");
    setModal(false);
  };

  const getListName = (text) => {
    return text
      .toLowerCase()
      .replace(/\s+/g, "_") // Replace whitespace with underscore
      .replace(/-+/g, "_"); // Replace dashes with underscore
  };

  const addTaskOnClick = (index) => {
    console.log("Add Task btn clicked");
    const newCard = {
      name: `New Card ${dataList[index].cards.length + 1}`,
      description: "Added dynamically",
    };

    setDataList((prev) => {
      const updatedPhases = prev.map((list) => ({
        ...list,
        cards: list.cards.map((card) => ({ ...card })), // deep copy of cards
      }));

      updatedPhases[index].cards.push(newCard);
      return updatedPhases;
    });
  };

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Projects":
        backToDashboard();
        break;
      case "Add List":
        addPhaseOnClick();
        break;
      case "Add Members":
        console.log("Add Members clicked");
        // addMembersOnClick(); // Implement when ready
        break;
      case "Assign Roles":
        console.log("Assign Roles clicked");
        // assignRolesOnClick(); // Implement when ready
        break;
      case "Logout":
        console.log("Logout btn clicked");
        handleLogout(navigate);
        break;
      default:
        console.warn("Unknown header button clicked");
    }
  };

  const backToDashboard = () => {
    navigate("/dashboard");
  };

  const addPhaseOnClick = () => {
    console.log("addPhaseOnClick clicked");

    const newPhase = {
      phase_name: `New Phase`,
      cards: [],
    };

    setDataList((prev) => {
      const updatedPhases = prev.map((list) => ({
        ...list,
        cards: list.cards.map((card) => ({ ...card })), // deep copy of cards
      }));
      updatedPhases.push(newPhase);
      return updatedPhases;
    });
  };

  const removePhaseOnClick = (index) => {
    console.log("removePhaseOnClick index: ", index);
    if (index != null) {
      setDataList((prev) => {
        const updatedPhases = [...prev];
        // Remove from source
        updatedPhases.splice(index, 1);
        return updatedPhases;
      });
    }
  };

  const taskMenuOnClick = () => {
    console.log("task menu onclick invoked");
  };

  const handleTaskCardDragStart = (e, srcPhaseIdx, srcCardIdx) => {
    console.log("drag start on task card");
    let taskCard = e.target;
    console.log("taskCard: ");
    console.log(taskCard);
    taskCard.style.opacity = 0.5;
    e.dataTransfer.setData(
      "application/json",
      JSON.stringify({ fromPhaseIdx: srcPhaseIdx, fromCardIdx: srcCardIdx })
    );
  };

  const handleTaskCardDragEnd = (e) => {
    console.log("drag end on task card");
    let taskCard = e.target;
    taskCard.style.opacity = 1;
  };

  const handleTaskContainerOnDrop = (e, targetPhaseIdx) => {
    console.log("on drop for task container");

    const { fromPhaseIdx, fromCardIdx } = JSON.parse(
      e.dataTransfer.getData("application/json")
    );

    // Prevent dropping into same list without movement
    if (fromPhaseIdx === targetPhaseIdx) return;

    setDataList((prev) => {
      const updatedPhases = prev.map((list) => ({
        ...list,
        cards: list.cards.map((card) => ({ ...card })), // deep copy of cards
      }));

      // Remove from source
      const [movedCard] = updatedPhases[fromPhaseIdx].cards.splice(
        fromCardIdx,
        1
      );

      console.log("movedCard: ", movedCard);

      // Add to target
      updatedPhases[targetPhaseIdx].cards.push(movedCard);

      // To remove undefined/null
      updatedPhases.forEach((list) => {
        list.cards = list.cards.filter(Boolean);
      });

      // console.log(updatedPhases);
      return updatedPhases;
    });
  };

  const handleTaskContainerDragOver = (e) => {
    console.log("on drag over for task container");
    e.preventDefault();
  };

  return (
    <BoardContainer>
      <MainHeader message="Workboard" />

      <BoardHeader
        projectName={"Project Demo"}
        btnLabels={headerBtnLabels}
        headerBtnOnClick={handleHeaderBtnClick}
      />
      <div className="ListContainer">
        {dataList.length > 0 ? (
          dataList.map((list, listIdx) => {
            return (
              <div className="BoardList" key={listIdx}>
                <div className="BoardListHeader">
                  {/* <p>{list.listName}</p> */}

                  <input
                    type="text"
                    defaultValue={list.listName}
                    onBlur={(e) => {
                      const updatedName = e.target.value;
                      console.log(`list name changed to ${updatedName}`);
                      // Update local state
                      /*setDataList((prev) =>
                        prev.map((item, idx) =>
                          idx === listIdx
                            ? { ...item, listName: updatedName }
                            : item
                        )
                      );*/
                      // Save to backend
                      /*updateListName(
                        listIdx ,
                        updatedName
                      );*/
                    }}
                  />

                  <BoardBtn
                    onClick={() => openModalForPhase(listIdx)}
                    label="X"
                    variant="close"
                  />
                </div>
                <div
                  className="TaskContainer"
                  onDrop={(e) => handleTaskContainerOnDrop(e, listIdx)}
                  onDragOver={handleTaskContainerDragOver}
                >
                  {/* <p>These are the cards in {data.phase_name}</p> */}
                  {list.cards.length > 0 ? (
                    list.cards.map((card, cardIdx) => {
                      return card != undefined ? (
                        <TaskCard
                          key={cardIdx}
                          handleTaskCardDragStart={(e) =>
                            handleTaskCardDragStart(e, listIdx, cardIdx)
                          }
                          handleTaskCardDragEnd={handleTaskCardDragEnd}
                          cardName={card.name}
                          cardDescription={card.description}
                          taskMenuOnClick={taskMenuOnClick}
                        />
                      ) : null;
                    })
                  ) : (
                    <p>Click on Add Task Button to add more tasks</p>
                  )}

                  <BoardBtn
                    onClick={() => addTaskOnClick(listIdx)}
                    label="Add Task"
                    style={{ marginBottom: "1%" }}
                  />
                </div>
              </div>
            );
          })
        ) : (
          <p>Click on Add Phase button to Add new phases</p>
        )}
      </div>
      {toggleModal && (
        <Modal
          modalMsg={"Do you want to remove this Phase ?"}
          modalYesOnClick={() => {
            removePhaseOnClick(phaseToRemoveIdx);
            closeModal();
          }}
          modalNoOnClick={() => {
            closeModal();
          }}
          onBackdropClick={() => closeModal()}
        />
      )}
    </BoardContainer>
  );
};
export default WorkBoard;
