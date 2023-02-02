package com.famillink.model.service;



import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService1 implements UserDetailsService {


    private final MemberMapper memberMapper;


    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return memberMapper.findUserByUid(Long.valueOf(uid)).map(this::addAuthorities) .orElseThrow(() -> new RuntimeException(uid + "> 찾을 수 없습니다."));
    }

    private Member addAuthorities(Member member) {
        member.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(member.getRole())));

        return member;
    }




}
