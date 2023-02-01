package com.famillink.model.service;


import java.io.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import org.apache.commons.io.IOUtils;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FaceDetectionImpl implements FaceDetection {
    public Map<String, Object> FaceCongnitive(String family, String src) throws Exception {

//        File temp = new File(src);
//        InputStream imageByte = new FileInputStream(temp);
//        byte[] bytes = IOUtils.toByteArray(imageByte);
//
//        imageByte.close();

//        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        //System.load("C:\\Users\\Hello\\Desktop\\ssafy_2th_git\\S08P12A208\\BE\\Spring\\src\\main\\java\\libs\\opencv_java3412.dll");
//        //System.loadLibrary("opencv_java320");
//
//        nu.pattern.OpenCV.loadShared(); //add this
//        //바로밑의 부분에 파일에 해당되는 경로를 넣어줍니다
//        //String image_name = "src/bag.jpg";
//        String image_name = src;
//
//        //쭉 변환 하는 과정입니다
//        Mat tempMat = null;
//        MatOfByte bytemat = new MatOfByte();
//        tempMat = Imgcodecs.imread(image_name, Imgcodecs.IMWRITE_PAM_FORMAT_RGB);
//
//        Imgcodecs.imencode(".jpg", tempMat, bytemat);
//        byte[] data = bytemat.toArray();

//        String encoded = Base64.getEncoder().encodeToString(bytes);
//
//        if(encoded == null){
//            throw new BaseException(ErrorMessage.NOT_EXIST_CONTENT);
//        }
//
//        encoded = new String(encoded.getBytes("utf-8"), "utf-8");


        //File temp = new File(src);
        //InputStream imageByte = URI.create(src).toURL().openStream();


        String image_name = src;

        File temp = new File(image_name);
        InputStream imageByte = null;
        try {
            imageByte = new FileInputStream(temp);
        } catch (FileNotFoundException e) {
            throw new BaseException(ErrorMessage.NOT_FOUND_FILE);
        }
        byte[] bytes = IOUtils.toByteArray(imageByte);

        imageByte.close();

        String encoded = Base64.getEncoder().encodeToString(bytes);

        encoded = new String(encoded.getBytes("utf-8"), "utf-8");
        //String result = encoded_after.toString().replaceAll(" ","+");
        Map<String, Object> map = new HashMap<>();
        map.put("img", encoded);


        //dict형태로 상대방에게 전달하여 flask 서버에서 판단 가능하게 해줍니다
        JSONObject resultObj = new JSONObject(map);


        //TODO; WebClient? (시간되면)
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

//        //HTTP 응답 코드 수신, 해당 부분으로써 성공인지 아닌지 판단 가능합니다
//        int responseCode = conn.getResponseCode();
//        if (responseCode == 400) {
//            System.out.println("400 : 명령을 실행 오류");
//            return false;
//        } else if (responseCode == 500) {
//            System.out.println("500 : 서버 에러.");
//            return false;
//        } else { //정상 . 200 응답코드 . 기타 응답코드
//            System.out.println(responseCode + " : 응답코드임");
//            return true;
//        }

        Map<String, Object> newtoken = new HashMap<>();
        //TODO; 응답 코드 기반으로 가족인지 아닌지 판단하여 결과, 멤버이름, 토큰 return


        return newtoken;
    }
}
