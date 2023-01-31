import './Weather.css'


function Weather() {
    const API_KEY = "da4992756d3a31cd9b0ded28d6534403";

    function onGeoOk(position) {
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;
    const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric&lang=kr`;
    fetch(url)
        .then((response) => response.json())
        .then((data) => {
        
        const city = document.querySelector("#weather span:first-child");
        const weather = document.querySelector("#weather span:last-child");
        city.innerText = data.name;
        console.log(data)
        const temp = Math.round(data.main.temp);
        weather.innerText = `${data.weather[0].description}      ${temp}º C`;
        const weatherIconSelect = document.querySelector("#weather img:nth-child(3)");
        const weatherIcon = data.weather[0].icon;
        weatherIconSelect.setAttribute("src", `http://openweathermap.org/img/wn/${weatherIcon}.png`)
    })
    }

    function onGeoError() {
    alert("Can't find you. No weather for you.");
    }

    navigator.geolocation.getCurrentPosition(onGeoOk, onGeoError)

    return (
        <div id="weather">
        <span className="city"></span>
        <br></br>
          <img className="weatherimg"
          alt="이미지가 없습니다."></img>
          <br></br>
          <span className="temp"></span>
      </div>
    )
}
        

export default Weather;