import BoardBtn from "./BoardBtn";
import "../style/BoardCardHeader.css";

const BoardCardHeader = ({ project, headerOnClick, closeBtnOnClick }) => {
  return (
    <div className="BoardCardHeader">
      <p onClick={headerOnClick}>Name: {project}</p>
      <BoardBtn onClick={closeBtnOnClick} label="X" variant="close" />
    </div>
  );
};
export default BoardCardHeader;
