import './App.css';

import MQTT from './components/MQTT';

import Clock from './components/Clock';
import Weather from './components/Weather';

import Main from './pages/Main';
import QR from './pages/QR';
import PlayVideo from './pages/PlayVideo';
import Record from './pages/Record';


import { Box } from '@mui/system';
import { Route, Routes } from 'react-router-dom';
import { useSelector } from 'react-redux';

function App() {
  const {me} = useSelector(state => ({
    me : state.valid.me
  }))

  // 라즈베리파이와 웹소켓을 연결되어, 신호를 주고받는다.
  // 초음파 센서 신호가 오면 안면 인식 화면으로 이동한다.
  // 초음파 센서 신호가 사라지면 메인 페이지로 돌아가는데, 어차피 디스플레이는 꺼진다.
  // App.js에서 제스쳐나 음성 신호 등이 들어오면 영상 녹화 화면으로 이동한다.

  const style = {
    paddingTop : '200px',
    height : '900px'
  }
  return (
    <div className="App">
      <MQTT/>

      <Box sx={{
        display: "flex",
        justifyContent:"space-between",
        p: 3
      }}>
        <Clock />
        <Weather />  
      </Box>
      
      <section style={style}>
      <div>{me ? <p>멤버가 로그인 되었습니다.</p> : <p>아직 멤버가 로그인 되지 않았어요</p>}</div>
      <Routes>
        <Route path="/" element= { <Main />} />
        <Route path="/qr" element={ <QR />} />
        <Route path="/playvideo" element={ <PlayVideo /> } />
        <Route path="/record" element={<Record/>} />
      </Routes>
      </section>

    </div>
  );
}

export default App;
