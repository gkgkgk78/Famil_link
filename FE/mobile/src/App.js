import "./App.css";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Main from "./pages/Main";
import SignUpSuccess from "./pages/SignUpSuccess";
import { Route, Routes } from "react-router-dom";
import Navbar from "./components/navbar/Navbar"; 
import Home from "./components/navbar/home/Home"
import AuthLayout from "./components/auth/AuthLayout";



function App() {
  const token = localStorage.getItem("faccesstoken")
  console.log(token)
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<AuthLayout component={<Main />} authenticated={token} />} />
        <Route path="/home" element={<AuthLayout component={<Home />} authenticated={token} />} />
        <Route path="/login" element={<Login />} />
        <Route path="/SignUp" element={<SignUp />} />
        <Route path="/SignUpSuccess" element={<SignUpSuccess />} />
      </Routes>
      <Navbar />
    </div>
  );
}

export default App;
