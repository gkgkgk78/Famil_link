import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Timer = () => {

  const [seconds, setSeconds] = useState(30);

  const nextPage = (seconds) => {
    if (parseInt(seconds) === 0) navigate("/")
  
  }

  const navigate = useNavigate();

  useEffect(() => {
    const countdown = setInterval(() => {
      if (parseInt(seconds) > 0) setSeconds(parseInt(seconds) - 1);
      if (parseInt(seconds) === 0) {
        clearInterval(countdown);
        navigate("/")
      }
    }, 1000);
    return () => clearInterval(countdown);
  }, [seconds]);

  return (
    <div>
          00:{seconds < 10 ? `0${seconds}` : seconds}
    </div>
  );
}
export default Timer;
