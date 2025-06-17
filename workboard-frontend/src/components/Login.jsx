import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../style/Login.css";
import WorkBoardHeader from "./WorkBoardHeader";
import BoardBtn from "./BoardBtn";
import BoardContainer from "./BoardContainer";

const Login = () => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const navigate = useNavigate();
  const gotoBoardOnClick = () => {
    navigate("/board");
  };
  return (
    <BoardContainer>
      <div className="Login-Container">
        <WorkBoardHeader message="Workboard" />
        <form className="Login-Form">
          <h2>Login</h2>
          <label>Username</label>
          <input type="text" required />
          <label>Password</label>
          <input type="password" required />
          <BoardBtn
            type="submit"
            text={isRegisterMode ? "Register" : "Login"}
            label="Login"
          />
        </form>
      </div>
    </BoardContainer>
  );
};

export default Login;
