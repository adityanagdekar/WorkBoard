import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import WorkBoard from "./WorkBoard";
import Login from "./Login";
// import "./App.css";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/board" element={<WorkBoard />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
