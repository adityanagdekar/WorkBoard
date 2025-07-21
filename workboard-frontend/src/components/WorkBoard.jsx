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
import useDebounce from "../hooks/usDebounce";

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

  const [dataLists, setDataLists] = useState([]);
  //   [
  //   {
  //     listName: "To-Do",
  //     cards: [{ name: "card 1", description: "This is the card 1" }],
  //   },
  //   {
  //     listName: "WIP",
  //     cards: [{ name: "card 2", description: "This is the card 2" }],
  //   },
  // ]
  const [toggleModal, setModal] = useState(false);

  const [listToRemoveIdx, setListToRemoveIdx] = useState(null);

  const [listName, setListName] = useState({});

  const [taskName, setTaskName] = useState("");

  const [taskDesc, setTaskDesc] = useState("");

  const debouncedListName = useDebounce(listName, 1000);
  const debouncedTaskName = useDebounce(taskName, 1000);
  const debouncedTaskDesc = useDebounce(taskDesc, 1000);

  // getting state-params passed
  // from ManageBoard.jsx to BoardGrid.jsx
  const { boardId } = useParams();
  const location = useLocation();
  const userId = location.state?.userId;

  // to get board-data --> (lists -> tasks)
  useEffect(() => {
    if (boardId && userId) {
      const getBoardData = async (boardId) => {
        try {
          const url = `http://localhost:8080/api/list/lists/${boardId}`;
          const configObj = {
            withCredentials: true,
          };
          const response = await axios.get(url, configObj);

          setDataLists(response.data.data);

          console.log("board-lists fetched: ", response.data.data);
        } catch (error) {
          console.log("Failed to get board-data: ", error);
        }
      };
      getBoardData(boardId);
    }
  }, [boardId, userId]);

  // to update list-name
  useEffect(() => {
    if (debouncedListName) {
      // Call backend updateList API here
      console.log("Saving list name: ", debouncedListName);
      saveListName(debouncedListName);
    }
  }, [debouncedListName]);

  // to update task-name
  useEffect(() => {
    if (debouncedTaskName) {
      console.log("Saving task name: ", debouncedTaskName);
      updateTaskAPI({ name: debouncedTaskName });
    }
  }, [debouncedTaskName]);

  // to udate task-desc.
  useEffect(() => {
    if (debouncedTaskDesc) {
      console.log("Saving task desc: ", debouncedTaskDesc);
      updateTaskAPI({ description: debouncedTaskDesc });
    }
  }, [debouncedTaskDesc]);

  const openModalForList = (index) => {
    console.log("openModalForList with index: ", index);
    setListToRemoveIdx(index);
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

  const handleHeaderBtnClick = (label) => {
    switch (label) {
      case "Projects":
        backToDashboard();
        break;
      case "Add List":
        addListOnClick();
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

  const handleListNameChange = (updatedName, id) => {
    console.log("updated list name: ", updatedName, "list id: ", id);
    setListName({
      id: id,
      name: updatedName,
    });
  };

  const saveListName = async (listNameObj) => {
    console.log("listname obj.: ", listNameObj);

    if (
      listNameObj.hasOwnProperty("id") &&
      listNameObj.hasOwnProperty("name")
    ) {
      // adding boardId to listName obj.
      listNameObj.boardId = boardId;

      // making api call
      try {
        const data = listName;
        const configObj = {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        };
        const response = await axios.post(
          "http://localhost:8080/api/list/save",
          data,
          configObj
        );
        console.log("list saved successfully: ", response.data);
      } catch (error) {
        console.error(
          "Saving List failed:",
          error.response?.data || error.message
        );
      }
    } else {
      console.log("list-name not yet updated");
    }
  };

  const addListOnClick = () => {
    console.log("addListOnClick clicked");

    const newList = {
      name: `New List`,
      cards: [],
    };
    console.log("newList: ", newList);

    /*setDataLists((prev) => {
      const updatedLists = prev.map((list) => ({
        ...list,
        cards: list.cards.map((card) => ({ ...card })), // deep copy of cards
      }));
      updatedLists.push(newList);
      return updatedLists;
    });*/

    setDataLists((prev) => {
      const updatedLists = prev.map((list) => {
        const updatedList = {
          ...list,
          cards: list.cards ? [...list.cards] : [],
        };
        return updatedList;
      });

      updatedLists.push(newList);
      return updatedLists;
    });
  };

  const addTaskOnClick = (index) => {
    console.log("Add Task btn clicked");
    const numOfCards =
      dataLists[index].cards == undefined ? 0 : dataLists[index].cards.length;

    const newCard = {
      name: `New Card ${numOfCards + 1}`,
      description: "Added dynamically",
    };
    console.log("newCard: ", newCard);

    setDataLists((prev) => {
      const updatedLists = prev.map((list, idx) => {
        let updatedList = {};
        if (idx === index) {
          const existingCards = list.cards ? [...list.cards] : [];
          updatedList = {
            ...list,
            cards: [...existingCards, newCard],
          };
        } else {
          updatedList = { ...list };
        }
        return updatedList;
      });
      console.log("updatedLists: ", updatedLists);
      return updatedLists;
    });
  };

  const removeListOnClick = (index) => {
    console.log("removeListOnClick index: ", index);
    /*if (index != null) {
      setDataLists((prev) => {
        const updatedPhases = [...prev];
        // Remove from source
        updatedPhases.splice(index, 1);
        return updatedPhases;
      });
    }*/
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

    /*const { fromPhaseIdx, fromCardIdx } = JSON.parse(
      e.dataTransfer.getData("application/json")
    );

    // Prevent dropping into same list without movement
    if (fromPhaseIdx === targetPhaseIdx) return;

    setDataLists((prev) => {
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
    */
  };

  const handleTaskContainerDragOver = (e) => {
    console.log("on drag over for task container");
    e.preventDefault();
  };

  const handleTaskNameChange = (updatedName, taskIdx) => {
    console.log("updated task name: ", updatedName);
    setTaskName((prev) => ({
      ...prev,
      taskIdx: updatedName,
    }));
  };

  const handleTaskDescChange = (updatedDesc, taskIdx) => {
    console.log("updated task desc: ", updatedDesc);
    setTaskDesc((prev) => ({
      ...prev,
      taskIdx: updatedDesc,
    }));
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
        {dataLists.length > 0 ? (
          dataLists.map((list, listIdx) => {
            return (
              <div className="BoardList" key={listIdx}>
                <div className="BoardListHeader">
                  {/* <p>{list.listName}</p> */}

                  <input
                    type="text"
                    defaultValue={list.name}
                    onChange={(e) => {
                      handleListNameChange(e.target.value, list.id);
                    }}
                  />

                  <BoardBtn
                    onClick={() => openModalForList(listIdx)}
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
                  {(list.cards?.length ?? 0) > 0 ? (
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
                          onNameChange={(e) => {
                            handleTaskNameChange(e.target.value, cardIdx);
                          }}
                          onDescChange={(e) => {
                            handleTaskDescChange(e.target.value, cardIdx);
                          }}
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
          modalMsg={"Do you want to remove this List ?"}
          modalYesOnClick={() => {
            removeListOnClick(listToRemoveIdx);
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
