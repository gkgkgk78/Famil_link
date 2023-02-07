package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ToFlaskImpl implements ToFlask {
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("text/txt");
    private final OkHttpClient client = new OkHttpClient();

    private final FlaskService flaskService;


    private final AccountMapper accountMapper;

    @Override
    public void send(Long uid, String path) throws Exception {

        Optional<Account> temp = accountMapper.findUserByUid(uid);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account = null;
        if (temp.isPresent()) {
            account = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        String pa = null;
        MediaType MEDIA_TYPE_PNG = null;
        String filename = "";

        if (path.equals("label")) {
            pa = flaskService.read_label_totext(account.getEmail());
            MEDIA_TYPE_PNG = MediaType.parse("text/txt");
            filename = "labels.txt";
        } else if (path.equals("model")) {
            pa = flaskService.read_model_totext(account.getEmail());
            MEDIA_TYPE_PNG = MediaType.parse("text/h5");
            filename = "model.h5";
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", account.getUid().toString())
                .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE_PNG, new File(pa)))
                .build();
        //http://localhost:5555/
        Request request = new Request.Builder()
                .header("title", account.getUid().toString())
                .url("http://localhost:5000/hihi")
 //               .url("http://flask-deploy:5000/hihi")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //System.out.println(response.body().string());
        }


    }
}
