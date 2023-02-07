package com.famillink.model.service;


import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.AccountMapper;
import com.famillink.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@RequiredArgsConstructor
@Service
@Primary


public class CustomUserDetailsService implements UserDetailsService {
    private final AccountMapper accountMapper;
    private final MemberMapper memberMapper;


    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {


        if (uid.startsWith("-")) {
            String ui = uid.substring(1);
            return memberMapper.findUserByUid(Long.valueOf(ui)).map(this::addAuthorities1).orElseThrow(() -> new RuntimeException(ui + "> 찾을 수 없습니다."));
        }


        return accountMapper.findUserByUid(Long.valueOf(uid))
                .map(this::addAuthorities)
                .orElseThrow(() -> new RuntimeException(uid + "> 찾을 수 없습니다."));


    }

    private Account addAuthorities(Account account) {
        account.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(account.getRole())));

        return account;
    }

    private Member addAuthorities1(Member member) {
        member.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(member.getRole())));

        return member;
    }


}
