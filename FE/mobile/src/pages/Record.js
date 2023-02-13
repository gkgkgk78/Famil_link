import React, { useState, useEffect } from "react";

function Record() {
  const [stream, setStream] = useState(null);
  const [video, setVideo] = useState(null);
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [chunks, setChunks] = useState([]);

  useEffect(() => {
    navigator.mediaDevices
      .getUserMedia({ video: true })
      .then(function (stream) {
        setStream(stream);
        setVideo(document.getElementById("video"));
        video.srcObject = stream;
      })
      .catch(function (error) {
        console.error("Error accessing camera:", error);
      });
  }, []);

  function startRecording() {
    setMediaRecorder(new MediaRecorder(stream));
    mediaRecorder.start();
    console.log("Stream", stream);

    mediaRecorder.addEventListener("dataavailable", (event) => {
      setChunks(chunks.concat(event.data));
    });
  }

  function stopRecording() {
    mediaRecorder.stop();

    const recordedBlob = new Blob(chunks, { type: "video/webm" });
    // You can now upload the recordedBlob to your server or store it locally using the localStorage API
  }

  return (
    <div>
      <video id="video" width="640" height="480" autoplay></video>
      <br />
      <button onClick={startRecording}>Start Recording</button>
      <button onClick={stopRecording}>Stop Recording</button>
    </div>
  );
}

export default Record;
