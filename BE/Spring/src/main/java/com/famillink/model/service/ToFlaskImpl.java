package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
@RequiredArgsConstructor
public class ToFlaskImpl implements ToFlask {
    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("text/txt");
    private final OkHttpClient client = new OkHttpClient();

    private final FlaskService flaskService;

    @Override
    public void send(Authentication authentication, String path) throws Exception {

        Account account = (Account) authentication.getPrincipal();
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
//                .url("http://localhost:5000/hihi")
                .url("http://flask-deploy:5000/hihi")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //System.out.println(response.body().string());
        }


    }
}
