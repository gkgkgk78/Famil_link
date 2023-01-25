package com.famillink.model.service;


import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;

import java.util.Map;

public interface ModelService {
    Member signup(Account account, Member userDto) throws Exception;

    Map<String, Object> login(Member loginDto) throws Exception;


    String refreshToken(Long uid, String token) throws Exception;

}
