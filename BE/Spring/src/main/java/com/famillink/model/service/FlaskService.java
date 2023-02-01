package com.famillink.model.service;

import com.famillink.model.domain.param.FlaskModelDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface FlaskService {

    //실제 db상에서 저장이 되지는 않을 것이다.
    void sender(FlaskModelDTO sender, MultipartFile file) throws Exception;

    InputStreamResource download(Long movie_uid) throws Exception;


    void setRead(Long movie_uid)throws Exception;




}
