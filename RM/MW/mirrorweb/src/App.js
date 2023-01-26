import './App.css';

import Clock from './components/Clock';
import Weather from './components/Weather';
import Caption from './components/Caption';

import Main from './pages/Main';
import LoginMirror from './pages/loginMirror';
import FaceValid from './pages/FaceValid';
import PlayVideo from './pages/PlayVideo';
import Record from './pages/Record';

import { Box } from '@mui/system';
import { Route, Routes } from 'react-router-dom';

function App() {

  // 라즈베리파이와 웹소켓을 연결되어, 신호를 주고받는다.
  // 초음파 센서 신호가 오면 안면 인식 화면으로 이동한다.
  // 초음파 센서 신호가 사라지면 메인 페이지로 돌아가는데, 어차피 디스플레이는 꺼진다.
  // App.js에서 제스쳐나 음성 신호 등이 들어오면 영상 녹화 화면으로 이동한다.

  return (
    <div className="App">
      <Box sx={{
        display: "flex",
        justifyContent:"space-between",
        p: 3
      }}>
        <Clock />
        <Weather />
      </Box>
      <footer></footer>
      <main>
      <div>
        <Routes>
          <Route path="/" element= { <Main />} />
          {/* <Route path="/loginmirror" element= { <LoginMirror />} /> */}
          <Route path="/facevalid" element={ <FaceValid /> } />
          <Route path="/playvideo" element={ <PlayVideo /> } />
          <Route path="/record" element={<Record/>} />
        </Routes>
      </div>
      </main>
      <footer> <Caption></Caption> </footer>

    </div>
  );
}

export default App;
