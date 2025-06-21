import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../style/Login.css";
import WorkBoardHeader from "./MainHeader";
import BoardBtn from "./BoardBtn";
import BoardContainer from "./BoardContainer";
import BoardCard from "./BoardCard";

const Login = () => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const navigate = useNavigate();
  const gotoBoardOnClick = () => {
    navigate("/dashboard");
  };
  return (
    <BoardContainer>
      {/* <div className="Login-Container"> */}

      <WorkBoardHeader message="Workboard" />
      <BoardCard>
        <form className="Login-Form ">
          <h2>Login</h2>
          <label>Username</label>
          <input type="text" required />
          <label>Password</label>
          <input type="password" required />
          <BoardBtn
            type="submit"
            text={isRegisterMode ? "Register" : "Login"}
            label="Login"
            onClick={gotoBoardOnClick}
          />
        </form>
      </BoardCard>

      {/* </div> */}
    </BoardContainer>
  );
};

export default Login;
