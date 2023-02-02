package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final FaceDetectionImpl face;

    @Transactional
    @Override
    //photo는 사용자가 찍은 사진 관련된 혹은 영상 관련된 경로를 의미를 한다
    public Member signup(String name, String nickname, Long t1) throws Exception {


        //바로 밑의 부분은 우선 영상 경로가 어떻게 될지 모르니 나중에 다시 복구 하도록 하자

//        boolean flag=face.send(member.toString(),new String());//가족 구분 path와 판단하고자 하는 이미지 경로를 넘겨주어야 함
//        if (flag) {
//            // 이미 등록이 된 가족의 얼굴 이라면 등록을 하지는 않을 것이다.
//            throw new BaseException(ErrorMessage.EXIST_ID);
//        }


        //찾은 회원이 가족 학습한 모델에 등록이 되었다면
        Member member = new Member(name, nickname);
        member.setUser_uid(t1.toString());

        try {
            mapper.signup(member);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            String salt = member.getUid().toString() + calendar.getTime();
            salt = (BCrypt.hashpw(salt, BCrypt.gensalt()));// salt를 설정하는 부분은 무엇일까?
            mapper.setSalt(member.getUid(), salt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ErrorMessage.EXIST_FACE);
        }

        return mapper.findUserByUid(member.getUid()).get();
    }


    @Override
    public Map<String, Object> login(Long uid) throws Exception {


//        if (!face.send("","")) {//로그인 하고자 할시에 없는 얼굴 등록 정보라면은 로그인이 불가능함
//            // 이미 등록이 된 가족의 얼굴 이라면 등록을 하지는 않을 것이다.
//            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);
//        }

        //찾은 회원의 uid로 찾기

        Member member1 = mapper.findUserByUid(uid)
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO));


        //존재하는 회원이고 비밀번호도 일치를 했다면, jwttokenprovider를 통해서 ,id와 role값을 전달하여 토큰을 생성후 프론트 단에 전달해줌
        String accessToken = jwtTokenProvider.createToken1(member1.getUid(), Collections.singletonList(member1.getRole()));
        String refreshToken = jwtTokenProvider.createRefresh1(member1.getUid(), Collections.singletonList(member1.getRole()));
        mapper.setRefreshToken(member1);

        return new HashMap<String, Object>() {{
            put("name", member1.getName());
            put("access-token", accessToken);
            put("refresh-token", refreshToken);
            put("uid", member1.getUid());
        }};
    }


    @Override
    public String refreshToken(Long uid, String token) throws Exception {
        Optional<Member> object = mapper.findUserByUid(uid);
        if (object.isPresent()) {
            Member member = object.get();
            if (token.equals(member.getRefresh_token())) {
                if (jwtTokenProvider.validateToken1(token))
                    return jwtTokenProvider.createToken1(member.getUid(), Collections.singletonList(member.getRole()));
                else
                    throw new BaseException(ErrorMessage.ACCESS_TOKEN_EXPIRE);
            } else {
                throw new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH);
            }
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }
    }


    @Override
    //두 멤버의 가족이 맞는지 아닌지 파악하는 부분을 의미를 함
    public Boolean findTogether(MovieSenderDTO sender) throws Exception {

        Member m, m1;
        m = mapper.findUserByUid(sender.getTo_member_uid()).get();
        m1 = mapper.findUserByUid(sender.getFrom_member_uid()).get();
        if (!m.getUser_uid().equals(m1.getUser_uid()))//계정 정보가 일치 하지 않을시에 처리 하고자 하는 상황
        {
            throw new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO);

        }

        return true;
    }

    @Override
    public Long findByUserName(String name) throws Exception {

        return mapper.findUserByName(name);
    }


}
