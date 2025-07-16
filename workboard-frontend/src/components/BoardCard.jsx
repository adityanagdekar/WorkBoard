import "../style/BoardCard.css";
const BoardCard = ({ children, style }) => {
  return (
    <div className="BoardCard" style={style}>
      {children}
    </div>
  );
};
export default BoardCard;
