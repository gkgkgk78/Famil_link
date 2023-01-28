package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.util.FaceDetection;
import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    //photo는 사용자가 찍은 사진 관련된 혹은 영상 관련된 경로를 의미를 한다
    public Member signup(Account account, Member member,String photo) throws Exception {

        FaceDetection temp= new FaceDetection();
        boolean flag=temp.send(account.toString(),new String());//가족 구분 path와 판단하고자 하는 이미지 경로를 넘겨주어야 함

        if (flag) {
            // 이미 등록이 된 가족의 얼굴 이라면 등록을 하지는 않을 것이다.
            throw new BaseException(ErrorMessage.EXIST_ID);
        }

        //등록될 회원의 경로명을 등록해 주는 부분입니다.
        member.setModel_path(new String());//나중에 경로명 지정하게 될시 설정 해주자.
        mapper.signup(member);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String salt = member.getUid().toString() + calendar.getTime();

        salt = (BCrypt.hashpw(salt, BCrypt.gensalt()));// salt를 설정하는 부분은 무엇일까?
        mapper.setSalt(member.getUid(), salt);

        return mapper.findUserByUid(member.getUid()).get();
    }



    @Override
    public Map<String, Object> login(Account account, Member member,String photo) throws Exception {

        FaceDetection temp= new FaceDetection();
        boolean flag=temp.send(account.toString(),new String());//가족 구분 path와 판단하고자 하는 이미지 경로를 넘겨주어야 함

        if (!flag) {//로그인 하고자 할시에 없는 얼굴 등록 정보라면은 로그인이 불가능함
            // 이미 등록이 된 가족의 얼굴 이라면 등록을 하지는 않을 것이다.
            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);
        }

        if (member.getLevel() == 0) {
            throw new BaseException(ErrorMessage.SIGNUP_LISTEN);
        }

        //존재하는 회원이고 비밀번호도 일치를 했다면, jwttokenprovider를 통해서 ,id와 role값을 전달하여 토큰을 생성후 프론트 단에 전달해줌
        String accessToken = jwtTokenProvider.createToken(member.getUid(), Collections.singletonList(member.getRole()));
        String refreshToken = jwtTokenProvider.createRefresh(member.getUid(), Collections.singletonList(member.getRole()));
        member.setRefresh_token(refreshToken);
        mapper.setRefreshToken(member);
        return new HashMap<String, Object>() {{
            put("name", member.getName());
            put("access-token", accessToken);
            put("refresh-token", refreshToken);
            put("uid", member.getUid());
        }};
    }






    @Override
    public String refreshToken(Long uid, String token) throws Exception {
        Optional<Member> object = mapper.findUserByUid(uid);
        if (object.isPresent()) {
            Member member = object.get();
            if (token.equals(member.getRefresh_token())) {
                if (jwtTokenProvider.validateToken(token))
                    return jwtTokenProvider.createToken(member.getUid(), Collections.singletonList(member.getRole()));
                else
                    throw new BaseException(ErrorMessage.ACCESS_TOKEN_EXPIRE);
            } else {
                throw new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH);
            }
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }
    }








}
