package com.famillink.model.service;

import com.famillink.model.domain.param.MovieSenderDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface MovieService {


    void sender(MovieSenderDTO sender, MultipartFile file) throws Exception;

    StreamingResponseBody download(Long movie_uid, HttpHeaders httpHeaders) throws Exception;


    void setRead(Long movie_uid) throws Exception;

    List<MovieSenderDTO> showMovieList(Long to_member_uid) throws Exception;
}
