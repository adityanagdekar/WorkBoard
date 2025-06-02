import { useState } from "react";
import BoardBtn from "./BoardBtn";
import "../style/BoardGrid.css";
import TaskCard from "./TaskCard";
import Modal from "./Modal";

const BoardGrid = () => {
  const priorities = ["P1", "P2"];
  const phases = ["To-Do", "WIP", "Test", "Review", "Deploy"];
  const headerBtnLabels = ["Add Phase", "Add Members", "Assign Roles"];

  const taskExists = (phase) => {
    if (["To-Do", "WIP", "Test"].includes(phase)) return true;
    return false;
  };

  const [dataList, setDataList] = useState([
    {
      phase_name: "To-Do",
      cards: [{ name: "card 1", description: "This is the card 1" }],
    },
    {
      phase_name: "WIP",
      cards: [{ name: "card 2", description: "This is the card 2" }],
    },
  ]);

  const [toggleModal, setModal] = useState(false);

  const [phaseToRemoveIdx, setPhaseToRemoveIdx] = useState(null);

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
      case "Add Phase":
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
      default:
        console.warn("Unknown header button clicked");
    }
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

  const handTaskContainerOnDrop = (e, targetPhaseIdx) => {
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
    <div className="BoardContainer">
      <div className="BoardHeader">
        <div className="BoardHeaderTitle">
          <h2>Project Demo</h2>
        </div>
        <div className="BoardHeaderBtnContainer">
          {headerBtnLabels.map((headerBtnLabel, index) => {
            return (
              <BoardBtn
                key={index}
                onClick={() => handleHeaderBtnClick(headerBtnLabel)}
                label={headerBtnLabel}
                variant="header"
              />
            );
          })}
        </div>
      </div>
      <div className="PhaseContainer">
        {dataList.length > 0 ? (
          dataList.map((phase, phaseIdx) => {
            return (
              <div className="BoardPhase" key={phaseIdx}>
                <div className="BoardPhaseHeader">
                  <p>{phase.phase_name}</p>
                  <BoardBtn
                    onClick={() => openModalForPhase(phaseIdx)}
                    label="X"
                    variant="close"
                  />
                </div>
                <div
                  className="TaskContainer"
                  onDrop={(e) => handTaskContainerOnDrop(e, phaseIdx)}
                  onDragOver={handleTaskContainerDragOver}
                >
                  {/* <p>These are the cards in {data.phase_name}</p> */}
                  {phase.cards.length > 0 ? (
                    phase.cards.map((card, cardIdx) => {
                      return card != undefined ? (
                        <TaskCard
                          key={cardIdx}
                          handleTaskCardDragStart={(e) =>
                            handleTaskCardDragStart(e, phaseIdx, cardIdx)
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
                </div>
                <BoardBtn
                  onClick={() => addTaskOnClick(phaseIdx)}
                  label="Add Task"
                />
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
    </div>
  );
};
export default BoardGrid;
