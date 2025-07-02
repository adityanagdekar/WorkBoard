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
  const [username, setUsername] = useState("");
  const [message, setMessage] = useState("");

  const gotoBoardOnClick = () => {
    navigate("/dashboard");
  };

  const loginBtnOnClick = async (e) => {
    e.preventDefault();
    console.log("clicked loginBtnOnClick");

    try {
      const response = await axios.post(
        "http://localhost:8080/api/user/login",
        {
          email,
          password,
        },
        {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      // JWT token recieved from backend
      const token = response.data.token;
      console.log("JWT token:", token);

      // storing token in localStorage
      localStorage.setItem("jwt", token);

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
      const response = await axios.post(
        "http://localhost:8080/api/user/register",
        {
          username,
          email,
          password,
        },
        {
          withCredentials: true,
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log(response.data);
      setMessage(response.data);
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
                onChange={(e) => setUsername(e.target.value)}
              />
            </>
          )}
          <label>Email Id</label>
          <input
            type="text"
            required
            onChange={(e) => setEmail(e.target.value)}
          />
          <label>Password</label>
          <input
            type="password"
            required
            onChange={(e) => setPassword(e.target.value)}
          />
          <BoardBtn type="button" label="Login" onClick={loginBtnOnClick} />
          <BoardBtn
            type="button"
            variant="register"
            label="Register"
            onClick={registerBtnOnClick}
          />
          <div className="Login-Message">{message}</div>
        </form>
      </BoardCard>

      {/* </div> */}
    </BoardContainer>
  );
};

export default Login;
