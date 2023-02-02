import React from "react";
import "./Weather.css";
import useCurrentLocation from "../hooks/useCurrentLocation";
import { geolocationOptions } from "../constants/geolocationOptions";
import Location from "./Location";

function WeatherHookTest() {
  const { location, error } = useCurrentLocation(geolocationOptions);

return (
    <div className="appContainer">
        <Location location={location} error={error} />
    </div>
  );
}

export default WeatherHookTest;