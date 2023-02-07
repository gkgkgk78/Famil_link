<<<<<<< HEAD
import './Weather.css'
import { WiDaySunny, WiSnowflakeCold, WiThunderstorm, WiDayRain, WiNightAltRain, WiNightClear, WiRain, WiNightAltCloudy, WiDayCloudy, WiCloud, WiCloudy, WiFog } from "weather-icons-react"
import { useState } from 'react';
=======
import React from 'react';
>>>>>>> origin/develop


function Weather() {
    const API_KEY = "da4992756d3a31cd9b0ded28d6534403";
    const [city, setCity] = useState('Seoul');
    const [temp, setTemp] = useState('5');
    // 날씨별로 react-icons 지정
    const [weatherIcon, setWeatherIcon] = useState([
            { 
                icon: '01d',
                img: <WiDaySunny size={30} color="white" /> 
            },
            {
                icon: '01n',
                img: <WiNightClear size={30} color="white" /> 
            },
            {
                icon: '02d',
                img: <WiDayCloudy size={30} color="white" /> 
            },
            {
                icon: '02n',
                img: <WiNightAltCloudy size={30} color="white" /> 
            },
            {
                icon: '03d',
                img: <WiCloud size={30} color="white" /> 
            },
            {
                icon: '03n',
                img: <WiCloud size={30} color="white" /> 
            },
            {
                icon: '04d',
                img: <WiCloudy size={30} color="white" /> 
            },
            {
                icon: '04n',
                img: <WiCloudy size={30} color="white" /> 
            },
            {
                icon: '09d',
                img: <WiRain size={30} color="white" /> 
            },
            {
                icon: '09n',
                img: <WiRain size={30} color="white" /> 
            },
            {
                icon: '10d',
                img: <WiDayRain size={30} color="white" /> 
            },
            {
                icon: '10n',
                img: <WiNightAltRain size={30} color="white" /> 
            },
            {
                icon: '11d',
                img: <WiThunderstorm size={30} color="white" /> 
            },
            {
                icon: '11n',
                img: <WiThunderstorm size={30} color="white" /> 
            },
            {
                icon: '13d',
                img: <WiSnowflakeCold size={30} color="white" /> 
            },
            {
                icon: '13n',
                img: <WiSnowflakeCold size={30} color="white" /> 
            },
            {
                icon: '50d',
                img: <WiFog size={30} color="white" /> 
            },
            {
                icon: '50n',
                img: <WiFog size={30} color="white" /> 
            },
    ]);
    const [findWeather, setFindWeather] = useState(<WiDaySunny size={30} color="white" />)
    const [weatherStatus, setWeatherStatus] = useState('');
    
    // 현재 위치를 찾는다면 onGeoOK 함수 실행
    function onGeoOk(position) {
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;
    const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric&lang=kr`;
    fetch(url)
<<<<<<< HEAD
    .then((response) => response.json())
    .then((data) => {
        // 온도는 반올림한다.
        setTemp(Math.round(data.main.temp));
        // 와이파이 기준 현재 동 표시, 기본값은 서울이다.
        setCity(data.name);
        // 날씨 아이콘 숫자를 보고 적절한 react-icons findWeather에 가져온다.
        setWeatherStatus(data.weather[0].icon)
        const findIndexNum = weatherIcon.findIndex(element => element.icon === weatherStatus)
        setFindWeather(weatherIcon[findIndexNum].img)
=======
        .then((response) => response.json())
        .then((data) => {

        const city = document.querySelector("#weather span:first-child");
        const weather = document.querySelector("#weather span:last-child");
        city.innerText = data.name;
        weather.innerText = `${data.weather[0].description} / ${data.main.temp}ºC`;
        })
>>>>>>> origin/develop
    }
    )
}

    // 현재 위치를 찾을 수 없다면 alert
    function onGeoError() {
        alert("Can't find you. No weather for you.");
    }

    // 현재 위치를 조회하는 함수, 실행되면 onGeoOK 함수 실행, 실패하면 onGeoError 함수 실행
    navigator.geolocation.getCurrentPosition(onGeoOk, onGeoError)

        return (
            <div className='weather'>
                <div className='city'>{city}</div>
            <div className='weatherimg'>{findWeather}</div>
            <div className='temp'>{temp}º C</div>
            </div>


        )
}
        

export default React.memo(Weather);