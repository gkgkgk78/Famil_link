package com.famillink.model.service;

import java.io.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FaceDetectionImpl implements FaceDetection {
    public Long isCongnitive(String family, String src) throws Exception {


        //바로밑의 부분에 파일에 해당되는 경로를 넣어줍니다
        String image_name = src;

        //쭉 변환 하는 과정입니다
        File temp = new File(image_name);
        InputStream imageByte = null;
        try {
            imageByte = new FileInputStream(temp);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = IOUtils.toByteArray(imageByte);

        imageByte.close();


        String encoded = Base64.getEncoder().encodeToString(bytes);

        Map<String, Boolean> response = new HashMap<>();


        encoded = new String(encoded.getBytes("utf-8"), "utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put("img", encoded);

        //dict형태로 상대방에게 전달하여 flask 서버에서 판단 가능하게 해줍니다
        JSONObject resultObj = new JSONObject(map);


        //post보내는 부분
        String host_url = "http://localhost:5555/";
        HttpURLConnection conn = null;
        URL url = new URL(host_url);

        conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");//POST GET
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "application/json");

        //POST방식으로 스트링을 통한 JSON 전송
        conn.setDoOutput(true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        System.out.println(resultObj.toString());
        bw.write(resultObj.toString());


        bw.flush();
        bw.close();

        //서버에서 보낸 응답 데이터 수신 받기

        InputStream inputStream = conn.getInputStream();
        Reader reader = new InputStreamReader(inputStream);

        StringBuilder result = new StringBuilder();

        for (int data = reader.read(); data != -1; data = reader.read()) {
            result.append((char) data);
        }

        long check = Long.parseLong(result.toString()); // 멤버 uid 반환. 0 이면 멤버로 등록돼있지 않음

        return check;
    }
}
