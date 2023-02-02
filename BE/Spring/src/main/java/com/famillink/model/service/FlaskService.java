package com.famillink.model.service;

import com.famillink.model.domain.user.Account;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FlaskService {

    //실제 db상에서 저장이 되지는 않을 것이다.
    void send_model(Account sender, MultipartFile file) throws Exception;

    void send_label(Account sender, MultipartFile file) throws Exception;


    String send_temp(Account sender, MultipartFile file) throws Exception;



    InputStreamResource read_model(String email) throws Exception;

    InputStreamResource read_label(String email) throws Exception;





}
