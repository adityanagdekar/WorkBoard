import axios from "axios";

const handleLogout = async (navigate) => {
  try {
    const url = "http://localhost:8080/api/user/logout";
    const data = {};
    const configObj = {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    };
    await axios.post(url, data, configObj);
    navigate("/");
  } catch (error) {
    console.error("Logout failed", error);
  }
};

export default handleLogout;
