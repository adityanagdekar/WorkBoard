import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import "../style/Login.css";
import WorkBoardHeader from "./MainHeader";
import BoardBtn from "./BoardBtn";
import BoardContainer from "./BoardContainer";
import BoardCard from "./BoardCard";

const Login = () => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [message, setMessage] = useState("");

  const gotoBoardOnClick = () => {
    navigate("/dashboard");
  };

  const loginBtnOnClick = async (e) => {
    e.preventDefault();
    console.log("clicked loginBtnOnClick");

    try {
      const data = {
        email,
        password,
      };
      const configObj = {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      };

      console.log("data: ", data);

      const response = await axios.post(
        "http://localhost:8080/api/user/login",
        data,
        configObj
      );
      // JWT token recieved from backend
      console.log("response.data: ", response.data);
      const userData = response.data.appUserData;
      console.log("userData: ", userData);
      localStorage.setItem("user", JSON.stringify(userData));

      setEmail("");
      setPassword("");
      navigate("/dashboard");
    } catch (error) {
      console.error("Login failed:", error.response?.data || error.message);
      alert("Invalid email or password");
    }
  };

  const registerBtnOnClick = async (e) => {
    e.preventDefault();
    console.log("clicked registerBtnOnClick");
    try {
      const url = "http://localhost:8080/api/user/register";
      const data = {
        name,
        email,
        password,
      };
      const configObj = {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      };
      console.log("data: ", data);
      const response = await axios.post(url, data, configObj);

      console.log(response.data);
      setMessage(response.data.message);

      setName("");
      setEmail("");
      setPassword("");
      setIsRegisterMode(false);
    } catch (error) {
      console.error(error);
      setMessage("Registration failed");
    }
  };

  return (
    <BoardContainer>
      {/* <div className="Login-Container"> */}

      <WorkBoardHeader message="Workboard" />
      <BoardCard>
        <form className="Login-Form ">
          <h2>Login</h2>
          {isRegisterMode && (
            <>
              <label>Username</label>
              <input
                type="text"
                required
                value={name} // <--- Recently, this was missing previously
                onChange={(e) => setName(e.target.value)}
              />
            </>
          )}
          <label>Email Id</label>
          <input
            type="text"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <label>Password</label>
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <BoardBtn
            type="button"
            label={isRegisterMode ? "Register" : "Login"}
            onClick={isRegisterMode ? registerBtnOnClick : loginBtnOnClick}
          />
          <BoardBtn
            type="button"
            variant="register"
            label={
              isRegisterMode
                ? "Already registered? Login"
                : "New user? Register"
            }
            onClick={() => setIsRegisterMode(!isRegisterMode)}
          />
          <div className="Login-Message">{message}</div>
        </form>
      </BoardCard>

      {/* </div> */}
    </BoardContainer>
  );
};

export default Login;
