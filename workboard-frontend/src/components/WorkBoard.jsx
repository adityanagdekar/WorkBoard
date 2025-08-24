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
import AddBoardModal from "./AddBoardModal";

/***********---custom styling---***********/
import "../style/WorkBoard.css";

/***********---utility libs.---***********/
import useAuthCheck from "../token/useAuthCheck";
import handleLogout from "../utility/handleLogout";
import useDebounce from "../hooks/usDebounce";
import { createStompClient } from "../utility/wsClient";

const WorkBoard = () => {
  // to do auth-check -> check whether user needs to login again or not
  useAuthCheck();
  const navigate = useNavigate();
  const stompRef = useRef(null);

  // getting state-params passed
  // from ManageBoard.jsx to BoardGrid.jsx
  const { boardId } = useParams();
  const location = useLocation();
  const userId = location.state?.userId;
  const membersMap = location.state?.membersMap;
  const hasManageAccess = location.state?.hasManageAccess
    ? location.state.hasManageAccess
    : false;
  // const boardName = location.state?.boardName;
  // const boardDesc = location.state?.boardDesc;

  const headerBtnLabels = ["Projects", "Add List", "Manage Board", "Logout"];

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

  const [boardName, setBoardName] = useState("");

  const [boardDesc, setBoardDesc] = useState("");

  const [listName, setListName] = useState({});

  const [taskName, setTaskName] = useState("");

  const [taskDesc, setTaskDesc] = useState("");

  // toggle for add-task-modal
  const [toggleAddTaskModal, setAddTaskModal] = useState(false);
  // toggle for add-board-modal
  const [toggleAddBoardModal, setAddBoardModal] = useState(false);

  // selected list id.
  const [selectedListId, setSelectedListId] = useState(-1);
  // select list idx.
  const [selectedListIdx, setSelectedListIdx] = useState(-1);
  // selected card obj.
  const [selectedTaskCard, setSelectedTaskCard] = useState({});
  // selected card's task-members.
  const [taskMembersMap, setTaskMembersMap] = useState([]);
  // to have a ref. of all the board-members
  const boardMembersMap = useRef({});

  // for drag events
  // src list & card idx.
  const srcListIdxRef = useRef(-1);
  const srcCardIdxRef = useRef(-1);
  const [hoveredListIdx, setHoveredListIdx] = useState(-1);
  const dragUpdateFlagRef = useRef(false);

  // debouncing for delayed-auto-saving of data
  const debouncedListName = useDebounce(listName, 1000);
  const debouncedTaskName = useDebounce(taskName, 1000);
  const debouncedTaskDesc = useDebounce(taskDesc, 1000);
  const debouncedLists = useDebounce(dataLists, 1000);

  // to set board-name & desc.
  useEffect(() => {
    if (location.state?.boardName) setBoardName(location.state.boardName);
    if (location.state?.boardDesc) setBoardDesc(location.state.boardDesc);
  }, [location.state?.boardName, location.state?.boardDesc]);

  const updateBoardDetails = (data) => {
    console.log("inside updateBoardDetails");
    if (data?.name && data?.name !== boardName) {
      setBoardName((prev) =>
        prev === data.name ? `${data.name} ` : data.name
      );
    }

    if (data?.description && data.description !== boardDesc) {
      setBoardDesc((prev) =>
        prev === data.description ? `${data.description} ` : data.description
      );
    }
  };

  const updateDataLists_withNewName = (data) => {
    console.log("inside updateDataLists_withNewName");
    setDataLists((prev) => {
      const updatedBoardLists = [...prev];

      const listIdx = updatedBoardLists.findIndex(
        (list) => list.id === parseInt(data.id)
      );

      if (listIdx !== -1) {
        updatedBoardLists[listIdx] = {
          ...updatedBoardLists[listIdx],
          name: data.name,
        };
      } else {
        console.log("List with ID not found in dataLists:", data.id);
      }

      return updatedBoardLists;
    });
  };

  const updateDataLists_withNewList = (data) => {
    console.log("inside updateDataLists_withNewList");
    setDataLists((prev) => {
      const updatedBoardLists = [...prev];

      let listIdx = -1;
      listIdx = updatedBoardLists.findIndex(
        (list) => list.id === parseInt(data.id)
      );

      if (listIdx === -1) {
        updatedBoardLists.push(data);
      }

      return updatedBoardLists;
    });
  };

  const updateDataLists_withNewTask = (data) => {};

  // to get board-data --> (lists -> tasks)
  useEffect(() => {
    let cancelled = false;

    // getting board-data
    const getBoardData = async (boardId) => {
      try {
        const url = `http://localhost:8080/api/list/lists/${boardId}`;
        const configObj = {
          withCredentials: true,
        };
        const response = await axios.get(url, configObj);

        if (cancelled) return;

        const boardLists = response.data.data;

        const updatedBoardLists = boardLists.map((list) => {
          const updatedList = {
            ...list,
            cards: list.cards?.map((card) => ({ ...card })) || [], // deep copy of cards
            canEdit: userId === list.userId,
          };
          return updatedList;
        });

        setDataLists(updatedBoardLists);

        console.log("board-lists fetched: ", updatedBoardLists);
        addToast("Board data fetched successfully", "success");
      } catch (error) {
        console.log("Failed to get board-data: ", error);
        addToast("Failed to get the board data", "error");
      }
    };

    // checking server-health & subscribing
    const healthCheckAndSubscribe = async () => {
      try {
        // checking server health ---> if its active or not
        const response = await axios.get(
          "http://localhost:8080/api/health/check"
        );

        if (response.status === 200 && !cancelled) {
          console.log("server is up & running");

          // subscribe process
          // create stomp client
          if (!stompRef.current) {
            stompRef.current = createStompClient();
          }
          const client = stompRef.current;

          // subscription topic destination
          const destination = `/topic/board.${boardId}`;
          let subscription = null;

          const subscribe = () => {
            try {
              subscription = client.subscribe(destination, (msg) => {
                try {
                  console.log("full stomp msg sent by server: ", msg);

                  const evt = JSON.parse(msg.body);
                  console.log("event: ", evt);
                  // Example server payload:
                  // { type: "UPSERT", boardId, payload: { id, name, description }, version }

                  console.log(`\n Event: ${evt.type} \n`);
                  if (evt.payload !== undefined) {
                    if (
                      evt?.type === "UPSERT" &&
                      parseInt(evt.boardId) === parseInt(boardId)
                    ) {
                      updateBoardDetails(evt.payload);
                    } else if (evt.type === "LIST_NAME_UPDATED") {
                      updateDataLists_withNewName(evt.payload);
                    } else if (evt.type === "LIST_ADDED") {
                      updateDataLists_withNewList(evt.payload);
                    } else if (evt.type === "TASK_ADDED") {
                      updateDataLists_withNewTask(evt.payload);
                    }
                  }

                  // You can expand this to handle list/card updates:
                  // if (evt.type === "LISTS_UPDATED") { ...patch setDataLists... }
                  // if (evt.type === "CARD_MOVED") { ...patch setDataLists... }
                } catch (err) {
                  console.error("Bad board event:", err, msg.body);
                }
              });
            } catch (err) {
              console.error("Subscribe failed:", err);
            }
          };

          // check if client is connected or not
          // based on that subscribe or wait for connect
          if (client.connected) {
            subscribe();
          } else {
            const prevOnConnect = client.onConnect;
            client.onConnect = (frame) => {
              subscribe();
              prevOnConnect?.(frame);
            };
          }
        }
      } catch (error) {
        console.error(
          "Server health check failed. Skipping WebSocket connect."
        );
      }
    };

    if (boardId && userId) {
      getBoardData(boardId);
      healthCheckAndSubscribe();
    }

    // cleanup on unmount / board change
    return () => {
      cancelled = true;
      try {
        subscription?.unsubscribe();
        stompRef.current?.deactivate();
      } catch {}
    };
  }, [boardId, userId, setDataLists, setBoardName, setBoardDesc]);

  // to update list-name
  useEffect(() => {
    if (debouncedListName) {
      // Call backend updateList API here
      console.log("Saving list name: ", debouncedListName);
      saveOrUpdateList(debouncedListName);
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
    if (debouncedLists.length > 0 && dragUpdateFlagRef.current) {
      console.log("debouncedList: ", debouncedLists);
      saveListsAndTasks();
    }
  }, [debouncedLists]);

  const saveListsAndTasks = async () => {
    console.log("inside saveListsAndTasks");

    try {
      const url = "http://localhost:8080/api/list/updateLists";
      const data = dataLists;
      const configObj = {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      };

      const response = await axios.post(url, data, configObj);

      const responseData = response.data;

      if (responseData) {
        console.log(responseData.message);
        if (responseData.success) {
          console.log("list saved successfully: ", responseData.data);
          addToast("Lists saved successfully", "sucess");
        }
      }
    } catch (error) {
      console.log("Saving List failed:", error.response?.data || error.message);
      addToast("Failed to save the Lists", "error");
    } finally {
      // reset the dragUpdateFlagRef
      dragUpdateFlagRef.current = false;
    }
  };

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
        if (hasManageAccess) addListOnClick();
        else addToast("You dont have manage acces", "error");
        break;
      case "Manage Board":
        console.log("Manage board clicked, hasManageAccess: ", hasManageAccess);
        if (hasManageAccess) showAddBoardModal();
        else addToast("You dont have manage acces", "error");
        break;
      case "Logout":
        console.log("Logout btn clicked");
        handleLogout(navigate);
        break;
      default:
        console.log("Unknown header button clicked");
    }
  };

  const showAddBoardModal = () => {
    console.log("show Board modal");

    if (membersMap && membersMap !== undefined) {
      console.log("using the useRef---membersMap: ", membersMap);
      boardMembersMap.current = membersMap;
    } else if (dataLists.length > 0) {
      dataLists.forEach((list) => {
        list.cards?.forEach((card) => {
          card.members?.forEach((member) => {
            if (member.hasOwnProperty("id"))
              membersMap[member.id] = member.role;
            else if (member.hasOwnProperty("userId"))
              membersMap[member.userId] = member.role;
          });
        });
      });

      console.log("using the dataLists---membersMap: ", membersMap);
    }
    boardMembersMap.current = membersMap;
    setAddBoardModal((prevState) => prevState || true);
  };

  const closeAddBoardModal = () => {
    console.log("close Project modal");
    setAddBoardModal((prevState) => prevState && false);
  };

  const handleBoardSaved = (updatedBoard) => {
    console.log("updatedBoard: ", updatedBoard);
    setBoardName(updatedBoard.name);
    setBoardDesc(updatedBoard.description);

    // also update router state so refresh keeps latest values
    navigate(".", {
      replace: true,
      state: {
        ...(location.state || {}),
        boardName: updatedBoard.name,
        boardDesc: updatedBoard.description,
      },
    });

    closeAddBoardModal();
  };

  const backToDashboard = () => {
    navigate("/dashboard");
  };

  const handleListNameChange = (updatedName, id) => {
    console.log("updated list name: ", updatedName, "list id: ", id);
    if (updatedName.length > 0) {
      setListName({
        id: id,
        name: updatedName,
      });
    }
  };

  const updateListNameLocally = (updatedName, id) => {
    setDataLists((prevLists) =>
      prevLists.map((list) =>
        list.id === id ? { ...list, name: updatedName } : list
      )
    );
  };

  const saveTaskName = async (taskNameObj) => {
    console.log("taskName obj.: ", taskNameObj);
  };

  const saveTaskDesc = async (taskNameObj) => {
    console.log("taskName obj.: ", taskNameObj);
  };

  const saveOrUpdateList = async (listObj) => {
    console.log(" inside saveOrUpdateList, listObj : ", listObj);

    if (listObj.hasOwnProperty("id") && listObj.hasOwnProperty("name")) {
      // adding boardId
      if (!listObj.hasOwnProperty("boardId")) listObj.boardId = boardId;
      // adding userId
      if (!listObj.hasOwnProperty("userId")) listObj.userId = userId;

      // making api call
      try {
        const url = "http://localhost:8080/api/list/save";
        const data = listObj;
        // listName;
        const configObj = {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        };
        const response = await axios.post(url, data, configObj);

        const responseData = response.data;

        const savedList = responseData.data;
        console.log("list saved successfully: ", savedList);

        setDataLists((prevLists) => {
          const updatedLists = [...prevLists];

          if (listObj.id && listObj.id > 0) {
            // this means list already exists & list-name was updated
            const idx = updatedLists.findIndex(
              (list) => list.id === listObj.id
            );
            // setting list data
            if (idx !== -1) {
              // updating list-name
              updatedLists[idx] = {
                ...updatedLists[idx],
                name: savedList.name,
              };
            }
          } else {
            // setting newList obj.
            const newList = {
              id: savedList.id,
              boardId: savedList.boardId,
              name: savedList.name,
              userId: userId,
              cards: [],
            };

            let listIdx = -1;
            listIdx = updatedLists.findIndex(
              (list) => list.id === parseInt(newList.id)
            );

            if (listIdx === -1) {
              // adding newly added list to the state var.
              updatedLists.push(newList);
            }
          }
          console.log("inside saveOrUpdateList, updatedLists: ", updatedLists);
          return updatedLists;
        });

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
      id: -1,
      name: `New List`,
      cards: [],
    };

    console.log("newList: ", newList);

    // addListToState(newList);
    saveOrUpdateList(newList);
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

  const addTaskToState = (listIdx, newCard, flag) => {
    console.log(
      "inside addTaskToState, newCard: ",
      newCard,
      "\n selectedListIdx: ",
      listIdx
    );

    // update the state
    setDataLists((prev) => {
      const updatedLists = prev.map((list, idx) => {
        if (idx !== listIdx) return { ...list };

        const existingCards = list.cards ? [...list.cards] : [];
        if (flag === 0) {
          // inserting a new card
          existingCards.push(newCard);
        } else if (flag === 1) {
          // update and existing card
          const cardIdx = existingCards.findIndex(
            (card) => card.id === newCard.id
          );
          if (cardIdx !== -1) {
            existingCards[cardIdx] = { ...newCard };
          } else if (cardIdx === -1) {
            // Reqd. card not found, fallback to original list
            console.warn("Card to update not found in list:", newCard.id);
            return list;
          }
        }

        // adding all changes
        const updatedList = {
          ...list,
          cards: existingCards,
        };

        return updatedList;
      });

      console.log("updatedLists: ", updatedLists);
      return updatedLists;
    });
  };

  /* TASK-CARD OBj.
  {
    "id": -1,
    "name": "Make new banner",
    "description": "Make new banner images for finance dept.",
    "isActive": true,
    "isCompleted": false,
    "members": [
        {
            "id": 1,
            "name": "John Doe",
            "isAdded": true,
            "role": 0
        },
        {
            "id": 10,
            "name": "Shree",
            "isAdded": true,
            "role": 1
        },
        {
            "id": 11,
            "name": "Niel",
            "isAdded": true,
            "role": 0
        }
    ],
    "userId": 10,
    "listId": 9
}
  */

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
    // diabling yes btn for deletion
    setIsDisabled(true);

    deleteBoardList(listId)
      .then((response) => {
        if (response.success === true) {
          console.log("dataLists before: ", dataLists);
          setDataLists((prev) => {
            const listIdx = dataLists.findIndex((list) => list.id === listId);
            console.log("listIdx: ", listIdx);
            const updatedBoardLists = [...prev];
            updatedBoardLists.splice(listIdx, 1);
            console.log("dataLists after: ", updatedBoardLists);
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
  };

  const deleteBoardList = async (id) => {
    const url = "http://localhost:8080/api/list/delete";
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

    let membersMap = {};
    if (cardObj.hasOwnProperty("members")) {
      membersMap = cardObj.members?.reduce((map, member) => {
        if (member.hasOwnProperty("id")) map[member.id] = member.role;
        else if (member.hasOwnProperty("userId"))
          map[member.userId] = member.role;
        return map;
      }, {});
    }

    console.log("membersMap: ", membersMap);

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

    // setting drag-list-flag
    dragUpdateFlagRef.current = true;

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
                    value={list.name}
                    onChange={(e) => {
                      updateListNameLocally(e.target.value, list.id);
                    }}
                    onBlur={(e) => {
                      handleListNameChange(e.target.value, list.id);
                    }}
                  />

                  {/*btn to delete list*/}
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
                          cardDescription={card.description}
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

      {toggleAddBoardModal && (
        <AddBoardModal
          boardId={boardId}
          name={boardName}
          desc={boardDesc}
          closeBtnOnClick={closeAddBoardModal}
          onBackDropClick={closeAddBoardModal}
          membersMap={boardMembersMap.current}
          addToast={addToast}
          removeToast={removeToast}
          onBoardSave={handleBoardSaved}
        />
      )}
    </BoardContainer>
  );
};
export default WorkBoard;
