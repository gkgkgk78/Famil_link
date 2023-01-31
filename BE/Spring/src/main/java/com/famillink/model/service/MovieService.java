package com.famillink.model.service;

import com.famillink.model.domain.param.MovieSenderDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface MovieService {


    void sender(MovieSenderDTO sender, MultipartFile file) throws Exception;

    InputStreamResource download(Long movie_uid) throws Exception;




}
