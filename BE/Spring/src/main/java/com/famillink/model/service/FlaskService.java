package com.famillink.model.service;

import com.famillink.model.domain.user.Account;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FlaskService {

    //실제 db상에서 저장이 되지는 않을 것이다.
    void send_model(Long uid, MultipartFile file) throws Exception;

    void send_label(Long uid, MultipartFile file) throws Exception;




    InputStreamResource read_model(Long uid) throws Exception;

    InputStreamResource read_label(Long uid) throws Exception;


    String read_label_totext(String email) throws Exception;

    String read_model_totext(String email) throws Exception ;



}
