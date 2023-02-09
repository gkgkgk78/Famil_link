import React, { useState } from "react";
import axios from "axios";
import './FamilyMemberRegister.css'

const FamilyMemberRegister = () => {
  const [formData, setFormData] = useState({
    name: "",
    nickname: "",
    imgfile:"",
  });
  const [selectedImage, setSelectedImage] = useState(null);
  const { name, nickname, imgfile } = formData;

  const onChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();

    const fatkn = localStorage.getItem('faccesstoken')


    axios.post(`http://i8a208.p.ssafy.io:3000/member/signup/${name}/${nickname}/`, {
        headers: {
            "Authorization": "Bearer "+fatkn.replaceAll('"', '')
        }
        }).then((res) => {
            console.log(res)
        }).cathch((err) => {
            console.log(err)
        });
}   

  return (
    <div className="member-registration">
      <h2>가족 멤버 등록</h2>
      {selectedImage && (
        <div>
        <img alt="not found" id="imgfile" width={"250px"} src={URL.createObjectURL(selectedImage)} />
        <br />
        <button onClick={()=>setSelectedImage(null)}>Remove</button>
        </div>
      )}
    <form className="card" onSubmit={onSubmit}>
        <div className="form-group">
        <input type="file" name="imgfile" onChange={(event) => {
            console.log(event.target.files[0]);
            setSelectedImage(event.target.files[0]);
        }}/>
        </div>
        <div className="form-group">
          <input
          id="name" 
            type="text"
            name="name"
            placeholder="이름"
            value={name}
            onChange={onChange}
          />
        </div>
        <div className="form-group">
          <input
            id="nickname"
            placeholder="별명"
            name="nickname"
            value={nickname}
            onChange={onChange}
          />
        </div>
        <button type="submit" className="save">Save</button>
      </form>
    </div>
  );
}
export default FamilyMemberRegister 