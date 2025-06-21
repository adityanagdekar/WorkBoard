import "../style/BoardHeader.css";
import BoardBtn from "./BoardBtn";

const BoardHeader = ({ projectName, btnLabels, headerBtnOnClick }) => {
  return (
    <div className="BoardHeader">
      <div className="BoardHeaderTitle">
        <h2>{projectName}</h2>
      </div>
      <div className="BoardHeaderBtnContainer">
        {btnLabels.map((headerBtnLabel, index) => {
          return (
            <BoardBtn
              key={index}
              onClick={() => headerBtnOnClick(headerBtnLabel)}
              label={headerBtnLabel}
              variant="header"
            />
          );
        })}
      </div>
    </div>
  );
};
export default BoardHeader;
