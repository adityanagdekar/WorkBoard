import "../style/BoardHeader.css";
import BoardBtn from "./BoardBtn";

const BoardHeader = ({ headerName, btnLabels, headerBtnOnClick }) => {
  return (
    <div className="BoardHeader">
      <div className="BoardHeaderTitle">
        <h2>{headerName}</h2>
      </div>
      <div className="BoardHeaderBtnContainer">
        {btnLabels.map((headerBtnLabel, index) => {
          return (
            <BoardBtn
              key={index}
              onClick={(e) => {
                e.preventDefault();
                headerBtnOnClick(headerBtnLabel);
              }}
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
