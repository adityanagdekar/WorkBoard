import { useState } from "react";
import WorkBoard from "./components/WorkBoard";
// import "./App.css";

function App() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <WorkBoard />
    </div>
  );
}

export default App;
