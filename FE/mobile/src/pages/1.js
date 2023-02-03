import React, { useState, useRef, useEffect } from 'react';

function CreateMember(props) {  
  const [rawImages, setRawImages] = useState([])
  const [rawPreviews, setRawPreviews] = useState([])
  const [filteredPreviews, setFilteredPreviews] = useState([])
  const canvasRef = useRef(null)

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
    setRawImages([...rawImages, file])
  }

  const addRawPreviews = (file) => {
    setRawPreviews([...rawPreviews, URL.createObjectURL(file)])
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

const FileInputView = ()=>{
  return <input type="file" onChange={handleFileInput} accept=".jpg, .jpeg, .png"></input>
}

const RawImagesView = ()=>{
  return <div className='imageCon'>
{  rawPreviews.map((item, idx) => <div key={"raw"+idx}><img src={item} alt="not valid" onClick={(e,item)=>createFilteredImage(e,item)}/></div>)}
  </div>
}



const MyCanvas = ()=>{
  return <canvas ref={canvasRef} style={{display:'none'}}></canvas>
}


  return (
    <div>
      <FileInputView />
      <RawImagesView />
      <MyCanvas />
    </div>
  );
  }

export default App;