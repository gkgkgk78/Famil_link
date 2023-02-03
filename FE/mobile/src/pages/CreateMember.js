import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {BsArrowLeft} from 'react-icons/fa'

function CreateMember(props) {
  
    const navigate = useNavigate();
    const [rawImages, setRawImages] = useState([])
    const [rawPreviews, setRawPreviews] = useState([])

    const handleFileInput = (e) => {
      const file = e.target.files[0]
      if(!isValidFile(file)){
        alert("is not valid file")
        return
      }
      addRawImages(file)
      addRawPreviews(file)
    }
  
    const addRawImages = (file) => {
      setRawImages([file])
    }

    const addRawPreviews = (file) => {
      setRawPreviews([URL.createObjectURL(file)])
    }
  
    const isValidFile = (file)=>{
      // todo file validation
      return true
    }
  
    useEffect(() => {
      // free memory when ever this component is unmounted
      if(rawPreviews != null){
        return () => rawPreviews.forEach(rp=>URL.revokeObjectURL(rp))
      }
  }, [rawPreviews])

  const fileInput = useRef(null);
  const handleButtonClick = e => {
    fileInput.current.click();
  };
  const FileInputView = ()=>{
    return (
        <>
    <input type="file" onChange={handleFileInput} accept=".jpg, .jpeg, .png" style={{ display: "none" }} ref={fileInput}></input>
    <button onClick={handleButtonClick}>파일 업로드</button>
    </>
    )
  }
  
  const RawImagesView = ()=>{
    return <div className='imageCon'>  {  rawPreviews.map((item, idx) => <div key={"raw"+idx}><img src={item} alt="not valid"/></div>)}
    </div>
  }

    return (
        <div>
           <header style = {{
            height : '100px',
            paddingTop : '35px',
            background : 'orange',
            color : 'white',
            display : 'flex'
           }}>
              <BsArrowLeft size='20' color='white' /><span> 프로필 추가</span>
            </header>
           <main>
            <RawImagesView />
            <FileInputView />

           

           </main>
        </div>
    );
}

export default CreateMember;