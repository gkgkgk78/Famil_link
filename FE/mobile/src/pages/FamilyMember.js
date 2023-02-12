import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const FamilyMember = () => {
  const [profile, setProfile] = useState({});

  useEffect(() => {
    const fetchProfile = async () => {
      const response = await fetch(
        "http://i8a208.p.ssafy.io:3000/account/member-list"
      );
      const data = await response.json();
      setProfile(data);
    };

    fetchProfile();
  }, []);

  return (
    <div>
      <h2>My Profile</h2>
      <p>Name: {profile.name}</p>
      <p>Email: {profile.email}</p>
      <p>Location: {profile.location}</p>
    </div>
  );
};

export default FamilyMember;
