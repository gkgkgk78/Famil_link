import React, { useState } from "react";
import "./portfolio.css";
import axios from "axios";
import Button from "../../common/Button";

const Portfolio = () => {
  const [showQR, setShowQR] = useState(false);
  const faccessToken = localStorage.getItem("faccesstoken");
  const qrCodeUrl = `https://chart.googleapis.com/chart?cht=qr&chl=${faccessToken}&chs=500x500`;

  const handleQRClick = () => {
    setShowQR(true);
  };

  return (
    <div className="qrcode" >
      {showQR ? (
        <img src={qrCodeUrl} alt="QR code" />
      ) : (
        <Button onClick={handleQRClick}>QR코드 생성</Button>
      )}
    </div>
  );
}

export default Portfolio;