import "./App.css";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Main from "./pages/Main";
import "./components/navbar/navbar.css"
import SignUpSuccess from "./pages/SignUpSuccess";
import { Route, Routes } from "react-router-dom";
import Navbar from "./components/navbar/Navbar"; 
import Home from "./components/navbar/home/Home"
import AuthLayout from "./components/auth/AuthLayout";
import FamilyMember from './pages/FamilyMember'
import FamilyMemberRegister from "./pages/FamilyMemberRegister";
import Todo from "./pages/Todo";


function App() {
  const token = localStorage.getItem("faccesstoken")
  console.log(token)
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<AuthLayout component={<Main />} authenticated={token} />} />
        <Route path="/login" element={<Login />} />
        <Route path="/SignUp" element={<SignUp />} />
        <Route path="/SignUpSuccess" element={<SignUpSuccess />} />
        <Route path="/FamilyMember" element={<FamilyMember />} />
        <Route path="/FamilyMemberRegister" element={<FamilyMemberRegister />}/>
        <Route path="/todo" element={<Todo />}/>
      </Routes>
      <Navbar />
    </div>
  );
}

export default App;
