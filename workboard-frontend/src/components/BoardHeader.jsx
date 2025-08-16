import { useEffect } from "react";
import "../style/BoardHeader.css";
import BoardBtn from "./BoardBtn";

const BoardHeader = ({ headerMsg, btnLabels, headerBtnOnClick }) => {
  useEffect(() => {
    console.log("BoardHeader re-rendered, new headerMsg:", headerMsg);
  }, [headerMsg]);

  const loggedIn_userId = JSON.parse(localStorage.getItem("user")).id;

  return (
    <div className="BoardHeader">
      <div className="BoardHeaderTitle">
        <h2>{headerMsg}</h2>
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
