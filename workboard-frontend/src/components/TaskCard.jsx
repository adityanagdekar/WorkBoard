import "../style/TaskCard.css";
import { EllipsisVertical } from "lucide-react";

const TaskCard = ({
  handleTaskCardDragStart,
  handleTaskCardDragEnd,
  cardName,
  cardDescription,
  taskMenuOnClick,
  onNameChange,
  onDescChange,
  listId,
}) => {
  return (
    <div
      className="TaskCard"
      draggable
      onDragStart={handleTaskCardDragStart}
      onDragEnd={handleTaskCardDragEnd}
    >
      <div className="TaskCardHeader">
        {/* <h4>{cardName}</h4> */}

        <input type="text" defaultValue={cardName} onChange={onNameChange} />

        <EllipsisVertical
          cursor="pointer"
          onClick={() => taskMenuOnClick(listId)}
        />
      </div>
      <div className="TaskCardContent">
        {/* <p>{cardDescription}</p> */}

        <textarea defaultValue={cardDescription} onChange={onDescChange} />
      </div>
    </div>
  );
};
export default TaskCard;

/*
<div
  className="TaskCard"
  key={cardIdx}
  draggable
  onDragStart={(e) =>
    handleTaskCardDragStart(e, phaseIdx, cardIdx)
  }
  onDragEnd={handleTaskCardDragEnd}
>
  <div className="TaskCardHeader">
    <h4>{card.name}</h4>
    <EllipsisVertical
      cursor="pointer"
      onClick={taskMenuOnClick}
    />
  </div>
  <div className="TaskCardContent">
    <p>{card.description}</p>
  </div>
</div>
*/
