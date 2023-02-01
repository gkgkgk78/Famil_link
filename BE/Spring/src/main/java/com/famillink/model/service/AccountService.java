package com.famillink.model.service;


import com.famillink.model.domain.user.Account;

import java.util.List;
import java.util.Map;

public interface AccountService {

    Account signup(Account account) throws Exception;

    Map<String, Object> login(Account loginAccount) throws Exception;



    List<Account> allMembers(Account loginAccount) throws Exception;

    String refreshToken(Long uid, String token) throws Exception;

    void sendSignupEmail(Account account) throws Exception;

    void resendCheckMail(Account loginAccount) throws Exception;

    void checkEmail(String token) throws Exception;

    void findMyPW(Account account) throws Exception;

}
