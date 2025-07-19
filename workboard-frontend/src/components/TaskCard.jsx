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
        {/* <h4>{cardName}</h4> */}

        <input
          type="text"
          defaultValue={cardName}
          /*onBlur={(e) => {
            const updatedName = e.target.value;
            setDataList((prev) => {
              const newList = [...prev];
              newList[listIdx].cards[cardIdx].name = updatedName;
              return newList;
            });
            updateCard(cardIdx , { name: updatedName });
          }}*/
          // className="card-title-input"
        />

        <EllipsisVertical cursor="pointer" onClick={taskMenuOnClick} />
      </div>
      <div className="TaskCardContent">
        {/* <p>{cardDescription}</p> */}

        <textarea
          defaultValue={cardDescription}
          /*onBlur={(e) => {
            const updatedDesc = e.target.value;
            setDataList((prev) => {
              const newList = [...prev];
              newList[listIdx].cards[cardIdx].description = updatedDesc;
              return newList;
            });
            updateCard(cardIdx , {
              description: updatedDesc,
            });
          }}*/
          // className="card-desc-input"
        />
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
