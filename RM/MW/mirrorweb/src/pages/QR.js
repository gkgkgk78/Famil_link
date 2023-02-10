import React, { useEffect, useRef } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';


function QR() {
    const qrMounted = useRef(false)

    const Navigate = useNavigate()

    const { familyaccesstoken } = useSelector(state => ({
        familyaccesstoken : state.valid.familyAccessToken
    }))

    useEffect(() => {
        if (!qrMounted.current) {
            qrMounted.current = true
        } else {
            Navigate("/")
        }
    },[familyaccesstoken])
    
    return (
        <div>
            QR코드로 로그인하여주세요
            <img src="http://api.qrserver.com/v1/create-qr-code/?color=000000&amp;bgcolor=FFFFFF&amp;data=testcode&amp;qzone=1&amp;margin=0&amp;size=400x400&amp;ecc=L" alt="qr code" />
        </div>
    );
}

export default QR;