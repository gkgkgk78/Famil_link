
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
        weather.innerText = `${data.weather[0].description} / ${data.main.temp}ºC`;
        console.log(data)
        })
    }

    function onGeoError() {
    alert("Can't find you. No weather for you.");
    }

    navigator.geolocation.getCurrentPosition(onGeoOk, onGeoError)

    return (
        <div id="weather">
        <span></span>
        <br></br>
        <span></span>
      </div>
    )
}
        

export default Weather;