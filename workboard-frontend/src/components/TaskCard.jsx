import "../style/TaskCard.css";
import { EllipsisVertical, Trash2 } from "lucide-react";

const TaskCard = ({
  handleTaskCardDragStart,
  handleTaskCardDragEnd,
  cardName,
  cardDescription,
  taskMenuOnClick,
  onNameChange,
  onDescChange,
  removeCardOnClick,
  openDeleteModal,
}) => {
  const handleTaskCardDelete = () => {
    openDeleteModal({
      msg: "Do you want delete this task-card ?",
      isOpen: true,
      onYesBtnClick: removeCardOnClick,
    });
  };

  return (
    <div
      className="TaskCard"
      draggable="true"
      onDragStart={handleTaskCardDragStart}
      onDragEnd={handleTaskCardDragEnd}
    >
      <div className="TaskCardHeader">
        {/* <h4>{cardName}</h4> */}

        <input type="text" value={cardName} onChange={onNameChange} />

        <EllipsisVertical cursor="pointer" onClick={taskMenuOnClick} />

        <Trash2 cursor="pointer" onClick={handleTaskCardDelete} />
      </div>
      <div className="TaskCardContent">
        {/* <p>{cardDescription}</p> */}

        <textarea value={cardDescription} onChange={onDescChange} />
      </div>
    </div>
  );
};
export default TaskCard;
