import BoardBtn from "./BoardBtn";
import "../style/BoardCardHeader.css";

const BoardCardHeader = ({ board, headerOnClick, closeBtnOnClick }) => {
  return (
    <div className="BoardCardHeader">
      <p onClick={headerOnClick}>Name: {board}</p>
      <BoardBtn onClick={closeBtnOnClick} label="X" variant="close" />
    </div>
  );
};
export default BoardCardHeader;
