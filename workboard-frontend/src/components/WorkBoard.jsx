/***********---external libs---***********/
import { useEffect, useState, useRef } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import axios from "axios";

/***********---components---***********/
import BoardBtn from "./BoardBtn";
import TaskCard from "./TaskCard";
import Modal from "./Modal";
import BoardContainer from "./BoardContainer";
import BoardHeader from "./BoardHeader";
import MainHeader from "./MainHeader";
import AddTaskModal from "./AddTaskModal";
import ToastMsg from "./ToastMsg";

/***********---custom styling---***********/
import "../style/WorkBoard.css";

/***********---utility libs.---***********/
import useAuthCheck from "../token/useAuthCheck";
import handleLogout from "../utility/handleLogout";
import useDebounce from "../hooks/usDebounce";

const WorkBoard = () => {
  // to do auth-check -> check whether user needs to login again or not
  useAuthCheck();
  const navigate = useNavigate();

  const headerBtnLabels = [
    "Projects",
    "Add List",
    "Add Members",
    "Assign Roles",
    "Logout",
  ];

  const [dataLists, setDataLists] = useState([]);

  const [toggleModal, setModal] = useState(false);

  const [modalConfig, setModalConfig] = useState({
    isOpen: false,
    message: "",
    onYesBtnClick: () => {},
  });

  const [toasts, setToasts] = useState([]);

  const [listToRemoveIdx, setListToRemoveIdx] = useState(null);

  const [listIdToDelete, setListIdToDelete] = useState(null);

  const [isDisabled, setIsDisabled] = useState(false);

  const [listName, setListName] = useState({});

  const [taskName, setTaskName] = useState("");

  const [taskDesc, setTaskDesc] = useState("");

  const [toggleAddTaskModal, setAddTaskModal] = useState(false);

  // selected list id.
  const [selectedListId, setSelectedListId] = useState(-1);
  // select list idx.
  const [selectedListIdx, setSelectedListIdx] = useState(-1);
  // selected card obj.
  const [selectedTaskCard, setSelectedTaskCard] = useState({});
  // selected card's task-members.
  const [taskMembersMap, setTaskMembersMap] = useState([]);

  // for drag events
  // src list & card idx.
  const srcListIdxRef = useRef(-1);
  const srcCardIdxRef = useRef(-1);
  const [hoveredListIdx, setHoveredListIdx] = useState(-1);

  // debouncing for delayed-auto-saving of data
  const debouncedListName = useDebounce(listName, 1000);
  const debouncedTaskName = useDebounce(taskName, 1000);
  const debouncedTaskDesc = useDebounce(taskDesc, 1000);
  const debouncedLists = useDebounce(dataLists, 1000);

  // getting state-params passed
  // from ManageBoard.jsx to BoardGrid.jsx
  const { boardId } = useParams();
  const location = useLocation();
  const userId = location.state?.userId;
  const boardName = location.state?.boardName;

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

          const boardLists = response.data.data;

          const updatedBoardLists = boardLists.map((list) => {
            const updatedList = {
              ...list,
              cards: list.cards?.map((card) => ({ ...card })) || [], // deep copy of cards
            };
            return updatedList;
          });

          setDataLists(updatedBoardLists);

          console.log("board-lists fetched: ", updatedBoardLists);
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
      saveTaskName({ name: debouncedTaskName });
    }
  }, [debouncedTaskName]);

  // to udate task-desc.
  useEffect(() => {
    if (debouncedTaskDesc) {
      console.log("Saving task desc: ", debouncedTaskDesc);
      saveTaskDesc({ description: debouncedTaskDesc });
    }
  }, [debouncedTaskDesc]);

  useEffect(() => {
    if (debouncedLists.length > 0)
      console.log("debouncedList: ", debouncedLists);
  }, [debouncedLists]);

  const openModalToDelete = (listId, idx) => {
    console.log("openModalToDelete for list with id: ", listId);
    setListToRemoveIdx(idx);
    setListIdToDelete(listId);
    setModal(true);
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
        addToast("Member added successfully");
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
        console.log("Unknown header button clicked");
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

  const saveTaskName = async (taskNameObj) => {
    console.log("taskName obj.: ", taskNameObj);
  };

  const saveTaskDesc = async (taskNameObj) => {
    console.log("taskName obj.: ", taskNameObj);
  };

  const saveListName = async (listNameObj) => {
    console.log("listname obj.: ", listNameObj);

    if (
      listNameObj.hasOwnProperty("id") &&
      listNameObj.hasOwnProperty("name")
    ) {
      // adding boardId to listName obj.
      listNameObj.boardId = boardId;
      listNameObj.userId = userId;

      // making api call
      try {
        const data = listNameObj;
        // listName;
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

        addToast("List saved successfully", "sucess");
      } catch (error) {
        console.error(
          "Saving List failed:",
          error.response?.data || error.message
        );

        addToast("Failed to save the List", "error");
      }
    } else {
      console.log("list-name not yet updated");
    }
  };

  const addListToState = (list) => {
    console.log("inside aaddListToState, list: ", list);
    setDataLists((prev) => {
      const updatedLists = prev.map((list) => {
        const updatedList = {
          ...list,
          cards: list.cards ? [...list.cards] : [],
        };
        return updatedList;
      });

      updatedLists.push(list);
      return updatedLists;
    });
  };

  const addListOnClick = () => {
    console.log("addListOnClick clicked");
    const newList = {
      name: `New List`,
      cards: [],
    };
    console.log("newList: ", newList);
    addListToState(newList);
  };

  const removeDummyTaskCard = (listIdx) => {
    console.log("inside removeDummyTaskCard, listIdx: ", listIdx);

    // Remove the dummyTaskCard & update the state
    setDataLists((prev) => {
      const updatedLists = prev.map((list, idx) => {
        let updatedList = {};

        if (idx === listIdx) {
          // getting filtered-card -> cards which does not have the "isDummy" attr.
          const filteredCards = (list.cards ? [...list.cards] : []).filter(
            (card) => !card.isDummy
          );

          // setting up the updatedList with filteredCards
          updatedList = {
            ...list,
            cards: filteredCards,
          };
        } else {
          updatedList = { ...list };
        }
        return updatedList;
      });
      console.log("Removed dummy task. Updated lists:", updatedLists);
      return updatedLists;
    });
  };

  const addTaskToState = (listIdx, newCard) => {
    console.log(
      "inside addTaskToState, taskCard: ",
      newCard,
      "\n selectedListIdx: ",
      listIdx
    );

    // update the state
    setDataLists((prev) => {
      const updatedLists = prev.map((list, idx) => {
        let updatedList = {};

        if (idx === listIdx) {
          const existingCards = list.cards ? [...list.cards] : [];
          updatedList = {
            ...list,
            cards: [...existingCards, newCard], // add the existingCards AND the newCard
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

  const addTaskOnClick = (listId) => {
    console.log("inside addTaskOnClick, listId: ", listId);

    const listIdx = dataLists.findIndex((list) => list.id === listId);

    console.log(
      "Add Task btn clicked, listIdx: ",
      listIdx,
      " listId: ",
      listId
    );
    const numOfCards =
      dataLists[listIdx].cards == undefined
        ? 0
        : dataLists[listIdx].cards.length;

    const newCard = {
      name: `Task Card ${numOfCards + 1}`,
      desc: "Added task description here",
      isActive: true,
      isCompleted: false,
      isDummy: true,
    };
    console.log("newCard: ", newCard);
    setSelectedListIdx(listIdx);
    addTaskToState(listIdx, newCard);
  };

  const removeListOnClick = (listId) => {
    console.log("removeListOnClick  listId: ", listId);
    const listIdx = dataLists.findIndex((list) => list.id === listId);

    // diabling yes btn for deletion
    setIsDisabled(true);

    if (listIdx != null) {
      // const response = await deleteBoardList(id);
      deleteBoardList(listId)
        .then((response) => {
          if (response.success === true) {
            setDataLists((prev) => {
              const updatedBoardLists = [...prev];
              updatedBoardLists.splice(listIdx, 1);
              return updatedBoardLists;
            });
            addToast("Board-list deleted successfully", "success");
          } else {
            addToast("Failed to delete the Board-list", "error");
          }
        })
        .catch((error) => {
          console.log(
            "Failed to delete the Board-list:",
            error.response?.data || error.message
          );
          addToast("Failed to delete the Board-list", "error");
        })
        .finally(() => {
          // enabling yes btn for deletion
          setIsDisabled(false);
        });
    } else {
      // enabling yes btn for deletion
      setIsDisabled(false);
    }
  };

  const deleteBoardList = (id) => {
    const url = "http://localhost:8080/api/list/delete";
    const data = { id: id };
    const headersObj = { "Content-Type": "application/json" };
    const configObj = {
      headers: headersObj,
      withCredentials: true,
    };
    // const response = await axios.post(url, data, configObj);
    axios
      .post(url, data, configObj)
      .then((response) => {
        console.log(
          "Response after deleting the board-list response.data: ",
          response.data
        );
        return response.data;
      })
      .catch((error) => {
        console.log(
          "Failed to delete board-list: ",
          error.response?.data || error.message
        );
        return { success: false };
      });
  };

  const removeCardOnClick = (listId, cardObj) => {
    const listIdx = dataLists.findIndex((list) => list.id === listId);
    console.log("removeCardOnClick listIdx: ", listIdx, " cardObj: ", cardObj);

    // diabling yes btn for deletion
    setIsDisabled(true);

    //  getting the card idx.
    const cardIdx = dataLists[listIdx]?.cards?.findIndex(
      (card) => card.id === cardObj.id
    );

    if (cardIdx != null && cardObj.id != null) {
      // const response = deleteTaskCard(cardId);

      deleteTaskCard(cardObj.id)
        .then((response) => {
          console.log("inside removeCardOnClick, response: ", response);
          if (response.success === true) {
            // updating state
            setDataLists((prev) => {
              const updatedBoardLists = prev.map((list) => ({
                ...list,
                cards: list.cards.map((card) => ({ ...card })), // deep copy of cards
              }));

              // Remove from source
              const [removedCard] = updatedBoardLists[listIdx].cards.splice(
                cardIdx,
                1
              );
              console.log("removedCard: ", removedCard);

              // To remove undefined/null
              updatedBoardLists.forEach((list) => {
                list.cards = list.cards.filter(Boolean);
              });

              console.log("updatedBoardLists: ", updatedBoardLists);
              return updatedBoardLists;
            });
            addToast("Task-card deleted successfully", "success");
          } else {
            addToast("Failed to delete the Task-card", "error");
          }
        })
        .catch((error) => {
          console.log(
            "Failed to delete the Task-card, error: ",
            error.response?.data || error.message
          );
          addToast("Failed to delete the Task-card", "error");
        })
        .finally(() => {
          // enabling yes btn for deletion
          setIsDisabled(false);
        });
    } else {
      // enabling yes btn for deletion
      setIsDisabled(false);
    }
  };

  const deleteTaskCard = async (id) => {
    const url = "http://localhost:8080/api/task/delete";
    const data = { id: id };
    const headersObj = { "Content-Type": "application/json" };
    const configObj = {
      headers: headersObj,
      withCredentials: true,
    };
    // const response = await axios.post(url, data, configObj);
    return axios
      .post(url, data, configObj)
      .then((response) => {
        console.log(
          "Response after deleting the task-card response.data: ",
          response.data
        );
        return response.data;
      })
      .catch((error) => {
        console.log(
          "Failed to delete task-card: ",
          error.response?.data || error.message
        );
        return { success: false };
      });
  };

  const taskMenuOnClick = (listId, cardObj) => {
    const listIdx = dataLists.findIndex((list) => list.id === listId);

    console.log(
      "task menu onclick invoked, listId: ",
      listId,
      "listIdx: ",
      listIdx,
      "\n cardObj: ",
      cardObj
    );
    <div className="W"></div>;

    const membersMap = cardObj.members.reduce((map, member) => {
      map[member.userId] = member.role;
      return map;
    }, {});

    setSelectedListIdx(listIdx);
    setSelectedListId(listId);
    setSelectedTaskCard(cardObj);
    setTaskMembersMap(membersMap);
    showAddTaskModal();
  };

  const showAddTaskModal = () => {
    console.log("show Add-Task modal");
    setAddTaskModal((prevState) => prevState || true);
  };

  const closeAddTaskModal = () => {
    console.log("close Add-Task modal");
    setAddTaskModal((prevState) => prevState && false);
  };

  const handleTaskCardDragStart = (e, srcListObj, srcCardObj) => {
    const listIdx = dataLists.findIndex((list) => list.id === srcListObj.id);
    const cardIdx = dataLists[listIdx]?.cards?.findIndex(
      (card) => card.id === srcCardObj.id
    );

    srcListIdxRef.current = listIdx;
    srcCardIdxRef.current = cardIdx;

    console.log("drag start on task card");

    // ✅ Required line to make drop target receive data
    e.dataTransfer.setData("text/plain", "dragging-task");

    let taskCard = e.target;
    console.log("taskCard: ");
    console.log(taskCard);
    taskCard.style.opacity = 0.5;
  };

  const handleTaskCardDragEnd = (e) => {
    console.log("drag end on task card");
    let taskCard = e.target;
    taskCard.style.opacity = 1;
    setHoveredListIdx(-1);
  };

  const handleTaskContainerOnDrop = (e, targetListIdx) => {
    console.log("on drop for task container");

    const srcListIdx = srcListIdxRef.current;
    const srcCardIdx = srcCardIdxRef.current;

    // Prevent dropping into same list without movement
    if (srcListIdx === targetListIdx) return;

    setDataLists((prev) => {
      const updatedDataLists = prev.map((list) => ({
        ...list,
        cards: [...list.cards.map((card) => ({ ...card }))], // deep copy of cards
      }));

      // Remove from source
      const [movedCard] = updatedDataLists[srcListIdx].cards.splice(
        srcCardIdx,
        1
      );

      console.log("movedCard: ", movedCard);

      // Add to target
      updatedDataLists[targetListIdx].cards.push(movedCard);

      // To remove undefined/null
      updatedDataLists.forEach((list) => {
        list.cards = list.cards.filter(Boolean);
      });

      console.log(
        "updatedDataLists[srcListIdx]: ",
        updatedDataLists[srcListIdx]
      );

      console.log(
        "updatedDataLists[targetListIdx]: ",
        updatedDataLists[targetListIdx]
      );

      return updatedDataLists;
    });
    setHoveredListIdx(-1);
  };

  const handleTaskContainerDragOver = (e, listIdx) => {
    console.log("on drag over for task container");
    e.preventDefault();
    setHoveredListIdx(listIdx);
  };

  const handleTaskContainerDragLeave = (e) => {
    console.log("on drag leave for task container");
    // e.preventDefault();
    if (!e.currentTarget.contains(e.relatedTarget)) setHoveredListIdx(-1);
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

  return (
    <BoardContainer>
      <MainHeader message="Workboard" />

      <BoardHeader
        headerMsg={boardName}
        btnLabels={headerBtnLabels}
        headerBtnOnClick={handleHeaderBtnClick}
      />

      <ToastMsg toasts={toasts} removeToast={removeToast} />

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

                  {/*btn to delete task*/}
                  <BoardBtn
                    // onClick={() => openModalToDelete(list.id, listIdx)}
                    onClick={() => {
                      openModal({
                        msg: "Do you want delete this board-list ?",
                        isOpen: true,
                        onYesBtnClick: () => {
                          // sending list.id
                          removeListOnClick(list.id);
                        },
                      });
                    }}
                    label="X"
                    variant="close"
                  />
                </div>
                <div
                  className={`TaskContainer ${
                    hoveredListIdx === listIdx ? "drag-over" : ""
                  }`}
                  onDrop={(e) => handleTaskContainerOnDrop(e, listIdx)}
                  onDragOver={(e) => handleTaskContainerDragOver(e, listIdx)}
                  onDragLeave={(e) => handleTaskContainerDragLeave(e)}
                >
                  {/* <p>These are the cards in {data.phase_name}</p> */}
                  {(list.cards?.length ?? 0) > 0 &&
                    list.cards.map((card, cardIdx) => {
                      return card != undefined ? (
                        <TaskCard
                          key={card.id}
                          handleTaskCardDragStart={(e) =>
                            handleTaskCardDragStart(e, list, card)
                          }
                          handleTaskCardDragEnd={handleTaskCardDragEnd}
                          cardName={card.name}
                          cardDescription={card.desc}
                          taskMenuOnClick={() => taskMenuOnClick(list.id, card)}
                          onNameChange={(e) => {
                            handleTaskNameChange(e.target.value, cardIdx);
                          }}
                          onDescChange={(e) => {
                            handleTaskDescChange(e.target.value, cardIdx);
                          }}
                          removeCardOnClick={() => {
                            removeCardOnClick(list.id, card);
                          }}
                          openDeleteModal={openModal}
                        />
                      ) : null;
                    })}

                  {/*btn to add more tasks*/}
                  <BoardBtn
                    onClick={() => {
                      taskMenuOnClick(list.id, {});
                    }}
                    label="Add Task"
                    style={{ marginBottom: "1%" }}
                  />
                </div>
              </div>
            );
          })
        ) : (
          <p>Click on Add List button to Add New Lists</p>
        )}
      </div>

      {/*Delete Modal*/}
      {modalConfig.isOpen && (
        <Modal
          modalMsg={isDisabled ? "Please wait..." : modalConfig.msg}
          modalYesOnClick={() => {
            // removeListOnClick(listToRemoveIdx, listIdToDelete);
            modalConfig.onYesBtnClick();
            closeModal();
          }}
          modalNoOnClick={() => {
            closeModal();
          }}
          onBackdropClick={() => closeModal()}
          disabledChk={isDisabled}
        />
      )}

      {/*Add task Modal*/}
      {toggleAddTaskModal && (
        <AddTaskModal
          closeBtnOnClick={closeAddTaskModal}
          onBackDropClick={closeAddTaskModal}
          boardId={boardId}
          listIdx={selectedListIdx}
          listId={selectedListId}
          cardObj={selectedTaskCard}
          taskMembersMap={taskMembersMap}
          addToast={addToast}
          removeToast={removeToast}
          removeDummyTaskCard={removeDummyTaskCard}
          addTaskToState={addTaskToState}
        />
      )}
    </BoardContainer>
  );
};
export default WorkBoard;
