import { Calendar } from 'react-calendar';
import React, {useEffect, useState} from 'react';
import "./calendar.css"
import moment from "moment";
 
const Calendars = () => {
 
 
  const [value, onChange] = useState(new Date());
  const marks = [
    "15-02-2023",
    "03-02-2023",
    "02-01-2023",
    "12-01-2023",
    "13-01-2023",
    "15-01-2023",
  ];
  return (
    <div>
      <Calendar
        onChange={onChange}
        value={value}
        locale="en-EN"
        tileClassName={({ date, view }) => {
          if (marks.find((x) => x === moment(date).format("DD-MM-YYYY"))) {
            return "highlight";
          }
        }}
      />
    </div>
  );
  }
export default Calendars