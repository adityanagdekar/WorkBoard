import "../style/TaskCard.css";
import { EllipsisVertical } from "lucide-react";

const TaskCard = ({
  handleTaskCardDragStart,
  handleTaskCardDragEnd,
  cardName,
  cardDescription,
  taskMenuOnClick,
}) => {
  return (
    <div
      className="TaskCard"
      draggable
      onDragStart={handleTaskCardDragStart}
      onDragEnd={handleTaskCardDragEnd}
    >
      <div className="TaskCardHeader">
        <h4>{cardName}</h4>
        <EllipsisVertical cursor="pointer" onClick={taskMenuOnClick} />
      </div>
      <div className="TaskCardContent">
        <p>{cardDescription}</p>
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
