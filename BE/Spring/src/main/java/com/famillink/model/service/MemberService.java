package com.famillink.model.service;


import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;

import java.util.Map;

public interface MemberService {
    Member signup(Member userDto, String photo) throws Exception;

    Map<String, Object> login(Member member, String photo) throws Exception;


    String refreshToken(Long uid, String token) throws Exception;

}
