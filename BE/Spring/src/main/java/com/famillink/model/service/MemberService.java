package com.famillink.model.service;


import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;

import java.util.Map;

public interface MemberService {
    Member signup(Account userDto, String name,String nickname) throws Exception;

    Map<String, Object> login(Account member, String photo) throws Exception;


    String refreshToken(Long uid, String token) throws Exception;

    Boolean findTogether(MovieSenderDTO sender) throws Exception;
}
