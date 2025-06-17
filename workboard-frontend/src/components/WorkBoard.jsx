import BoardGrid from "./BoardGrid";
import WorkBoardHeader from "./WorkBoardHeader";
const WorkBoard = () => {
  return (
    <div>
      <WorkBoardHeader message="Workboard" />
      <BoardGrid />
    </div>
  );
};
export default WorkBoard;
