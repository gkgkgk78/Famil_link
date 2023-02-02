import React from "react";
import PropTypes from "prop-types";

const Location = ({ location, error }) => {
  // const API_KEY = "da4992756d3a31cd9b0ded28d6534403";

  // console.log(location['latitude']);
    // const lat = location['latitude'];
    // const lon = location['longitude'];
    // const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${API_KEY}&units=metric`;
    
    // fetch(url)
    //   .then((response) => response.json())
    //   .then((data) => {
    //     const weather = document.querySelector("#weather span:first-child");
    //     const city = document.querySelector("#weather span:last-child");
    //     city.innerText = data.name;
    //     weather.innerText = `${data.weather[0].main} / ${data.main.temp}`;
    //   }); 
  return (
    <div>
      {location ? (
        <code>
          Latitude: {location.latitude}, Longitude: {location.longitude}
        </code>
      ) : (
        <p>Loading...</p>
        )}
      {error && <p className="errorMessage">Location Error: {error}</p>}
    </div>
  );
};

Location.propTypes = {
  location: PropTypes.object,
  error: PropTypes.string,
};

export default Location;
