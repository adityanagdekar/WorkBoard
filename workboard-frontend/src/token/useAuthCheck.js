import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const useAuthCheck = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const checkJwtSession = async () => {
      try {
        const url = "http://localhost:8080/api/user/session";
        const configObj = { withCredentials: true };
        const response = await axios.get(url, configObj);

        localStorage.setItem("email", response.data);
      } catch (err) {
        console.log("JWT expired or invalid. Redirecting to login...", err);
        localStorage.clear();
        navigate("/");
      }
    };

    checkJwtSession();
  }, []);
};

export default useAuthCheck;
