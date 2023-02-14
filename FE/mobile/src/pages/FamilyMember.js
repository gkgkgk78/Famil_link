import React, { useState, useEffect } from "react";
import axios from "axios";

const FamilyMember = () => {
  const [profile, setProfile] = useState([]);
  const [photoUrls, setPhotoUrls] = useState({});
  const [loading, setLoading] = useState(false);
  const result = []

  const token = localStorage.getItem("faccesstoken").replace(/"/gi, '');
  // const name = "토르"
  useEffect(() => {
    axios.get('http://i8a208.p.ssafy.io:3000/account/member-list', {
      headers: {
        "Authorization": `Bearer ${token}`,
      }
    })
    .then(res => {
      setProfile(res.data.list);
      console.log(profile)
    }).catch(err => {
      console.log(err)
    })
  }, [setProfile]);


  const requests = profile.map(({ name }) =>
    axios.get(`http://i8a208.p.ssafy.io:3000/photo/${name}`, {
      responseType:'blob',
      headers: {
            "Authorization": `Bearer ${token}`,
          },
        }).then(res => {
          const url = window.URL.createObjectURL(new Blob([res.data], { type: res.headers['content-type'] } ));
          setPhotoUrls(url)
        }).catch(err => {
          console.log(err)
        })
  )

  return (
    <div>
      {requests}
      {/* <img src={photoUrls} alt="load_fail"></img> */}
    </div>
  );
};

export default FamilyMember;