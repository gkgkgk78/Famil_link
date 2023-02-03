package com.famillink.model.service;


import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface MemberService {
    Member signup(String name,String nickname,Long tt) throws Exception;

    Map<String, Object> login(Long uid) throws Exception;


    String refreshToken(Long uid, String token) throws Exception;

    Boolean findTogether(MovieSenderDTO sender) throws Exception;

    Long findByUserName(String name) throws Exception;
}
