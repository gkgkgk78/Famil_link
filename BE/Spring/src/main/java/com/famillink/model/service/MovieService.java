package com.famillink.model.service;

import com.famillink.model.domain.param.MovieDTO;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Member;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface MovieService {


    void sender(MovieSenderDTO sender, MultipartFile file) throws Exception;

    StreamingResponseBody download(Long movie_uid, HttpHeaders httpHeaders, Authentication authentication) throws Exception;


    void setRead(Long movie_uid) throws Exception;

    List<MovieDTO> showMovieList(Long member_to) throws Exception;


    List<Long> getAccountList(Member member)throws Exception;


}
