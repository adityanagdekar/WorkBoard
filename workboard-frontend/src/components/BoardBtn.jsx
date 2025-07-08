import "../style/BoardBtn.css";

const BoardBtn = ({ onClick, label, variant, style }) => {
  return (
    <div
      className={`BoardBtn ${variant ? `BoardBtn--${variant}` : ""}`}
      style={style}
    >
      <button onClick={onClick}>{label}</button>
    </div>
  );
};

export default BoardBtn;
