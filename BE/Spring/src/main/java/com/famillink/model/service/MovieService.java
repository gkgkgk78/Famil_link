package com.famillink.model.service;

import com.famillink.model.domain.param.MovieSenderDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface MovieService {


    void sender(MovieSenderDTO sender, MultipartFile file) throws Exception;

    StreamingResponseBody download(Long movie_uid, HttpHeaders httpHeaders) throws Exception;


    void setRead(Long movie_uid)throws Exception;

}
