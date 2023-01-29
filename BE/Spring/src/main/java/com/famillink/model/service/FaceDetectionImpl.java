package com.famillink.model.service;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FaceDetectionImpl implements FaceDetection {
    public boolean send(String family, String src) {


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.load("C:\\Users\\Hello\\Desktop\\ssafy_2th_git\\S08P12A208\\BE\\Spring\\src\\main\\java\\libs\\opencv_java3412.dll");

        //바로밑의 부분에 파일에 해당되는 경로를 넣어줍니다
        //String image_name = "src/bag.jpg";
        String image_name = src;

        //쭉 변환 하는 과정입니다
        Mat tempMat = null;
        MatOfByte bytemat = new MatOfByte();
        tempMat = Imgcodecs.imread(image_name, Imgcodecs.IMWRITE_PAM_FORMAT_RGB);

        Imgcodecs.imencode(".jpg", tempMat, bytemat);
        byte[] data = bytemat.toArray();

        String encoded = Base64.getEncoder().encodeToString(data);
        try {
            encoded = new String(encoded.getBytes("utf-8"), "utf-8");
            //String result = encoded_after.toString().replaceAll(" ","+");
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String returnMsg = in.readLine();
            StringBuffer resul = new StringBuffer();

            //리턴받은 성공 메시지 출력을 위한 부분
            for (int i = 0; i < returnMsg.length(); i++) {
                if (returnMsg.charAt(i) == '\\' && returnMsg.charAt(i + 1) == 'u') {
                    Character c = (char) Integer.parseInt(returnMsg.substring(i + 2, i + 6), 16);
                    resul.append(c);
                    i += 5;
                } else {
                    resul.append(returnMsg.charAt(i));
                }
            }


            System.out.println("응답메시지 : " + resul);

            //HTTP 응답 코드 수신, 해당 부분으로써 성공인지 아닌지 판단 가능합니다
            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                System.out.println("400 : 명령을 실행 오류");
                return false;
            } else if (responseCode == 500) {
                System.out.println("500 : 서버 에러.");
                return false;
            } else { //정상 . 200 응답코드 . 기타 응답코드
                System.out.println(responseCode + " : 응답코드임");
                return true;
            }

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
