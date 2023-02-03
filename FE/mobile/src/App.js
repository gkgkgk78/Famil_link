import './App.css';
import Login from './pages/Login';
import SignUp from './pages/SignUp';
import { Route, Routes } from 'react-router-dom';
import ProfilePage from './pages/ProfilePage';
import CreateMember from './pages/CreateMember';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/login" element= { <Login /> } />
        <Route path="/SignUp" element= { <SignUp />} />
        <Route path="/profilepage" element= { <ProfilePage />} />
        <Route path="/createmember" element= { <CreateMember />} />
      </Routes>
    </div>
  );
}

export default App;
