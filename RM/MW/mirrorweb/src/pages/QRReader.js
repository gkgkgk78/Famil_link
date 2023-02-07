import React, {useRef, useState, useEffect} from 'react';
import { QrReader } from 'react-qr-reader';
import { useDispatch } from 'react-redux';
import { setFamilyAccessToken, setFamilyRefreshToken } from '../modules/valid';
import axios from 'axios';

function QRReader() {
    const [QRData, setQRData] = useState('No result');
    const qrMounted = useRef(false)

    const dispatch = useDispatch();
    const saveFRToken = frtoken => dispatch(setFamilyRefreshToken(frtoken))
    const saveFAToken = fatoken => dispatch(setFamilyAccessToken(fatoken))

    useEffect(() => {
      if (!qrMounted.current) {
        qrMounted.current = true
      } else {
        let jsonData = JSON.parse(QRData)
        console.log(jsonData)
        console.log(jsonData.email)
        axios({
          method: "post",
          url: "http://i8a208.p.ssafy.io:3000/account/login",
          data: {
            "email": jsonData.email,
            "pw": jsonData.pw
          }
        })
        .then ((res) => {
          saveFRToken(res.data["access-token"])
          saveFAToken(res.data["refresh-token"])
        })
        .catch ((err) => {
          console.log(err)
        })
      }
    },[QRData])

    return (
    <div>
      <div className='framewrapper'>
      <QrReader
        onResult={(result) => {
          if (!!result) {
            setQRData(result?.text);
          }
        }}
        containerStyle={{
          heigth:"50%"
        }}
        videoContainerStyle={{
             "margin" : "auto",
             "paddingTop": "30%",
             height: '75%' ,
             width : '50%',
            }}
      />
      </div>
    </div>
    );
}

export default QRReader;