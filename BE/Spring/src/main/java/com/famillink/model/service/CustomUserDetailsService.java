package com.famillink.model.service;


import com.famillink.model.domain.user.Account;
import com.famillink.model.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountMapper accountMapper;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return accountMapper.findUserByUid(Long.valueOf(uid))
                .map(this::addAuthorities)
                .orElseThrow(() -> new RuntimeException(uid + "> 찾을 수 없습니다."));
    }

    private Account addAuthorities(Account account) {
        account.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(account.getRole())));

        return account;
    }
}
