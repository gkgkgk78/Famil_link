import React, { useState, useEffect } from "react";
import axios from "axios";
import styled from "styled-components";

const StyledImg = styled.img`
  width: 100px;
  height: 100px;
  border-radius: 100%;
  margin-bottom: 1.5rem;
`;

const FamilyMember = () => {
  const [profile, setProfile] = useState({});
  const [photoUrls, setPhotoUrls] = useState({});
  const [loading, setLoading] = useState(false);
  const [families, setFamilies] = useState({});
  const result = [];

  const token = localStorage.getItem("faccesstoken").replace(/"/gi, "");
  // const name = "토르"
  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axios.get(
          "http://i8a208.p.ssafy.io:3000/account/member-list",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setProfile(res.data.list);
        localStorage.setItem("profile", JSON.stringify(res.data.list));
      } catch (err) {
        console.log(err);
      }
    };
    console.log(profile);
    if (localStorage.getItem("profile")) {
      setProfile(JSON.parse(localStorage.getItem("profile")));
    } else {
      fetchData();
    }
  }, []);

  const requests = Object.values(profile).map(({ name }) => {
    if (!photoUrls[name]) {
      axios
        .get(`http://i8a208.p.ssafy.io:3000/photo/${name}`, {
          responseType: "blob",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then((res) => {
          const url = window.URL.createObjectURL(
            new Blob([res.data], { type: res.headers["content-type"] })
          );
          setPhotoUrls((prevUrls) => ({ ...prevUrls, [name]: url }));
        })
        .catch((err) => {
          console.log(err);
        });
    }
  });

  const handleProfile = (event, name, user_id) => {
    event.preventDefault();
    setFamilies({ name, user_id });
  };

  return (
    <div>
      {Object.values(profile).map(({ name }) => {
        if (photoUrls[name]) {
          return (
            <div key={name}>
              <StyledImg
                src={photoUrls[name]}
                alt={name}
                onClick={(event) => handleProfile(event, name, profile.user_id)}
              />
              <p>{name}</p>
            </div>
          );
        }
        return null;
      })}
    </div>
  );
};

export default FamilyMember;
