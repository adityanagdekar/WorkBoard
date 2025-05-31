import "../style/BoardBtn.css";

const BoardBtn = ({ onClick, label, variant }) => {
  return (
    <div className={`BoardBtn ${variant ? `BoardBtn--${variant}` : ""}`}>
      <button onClick={onClick}>{label}</button>
    </div>
  );
};

export default BoardBtn;
