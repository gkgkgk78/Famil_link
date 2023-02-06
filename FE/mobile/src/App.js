import "./App.css";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Main from "./pages/Main";
import SignUpSuccess from "./pages/SignUpSuccess";
import AuthLayout from "./components/auth/AuthLayout";
import { Route, Routes } from "react-router-dom";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route element={<AuthLayout />}>
          <Route path="/" element={<Main />} />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/SignUp" element={<SignUp />} />
        <Route path="/SignUpSuccess" element={<SignUpSuccess />} />
      </Routes>
    </div>
  );
}

export default App;
