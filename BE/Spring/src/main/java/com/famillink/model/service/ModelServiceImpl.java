package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.UserDTO;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.util.FaceDetection;
import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;


@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final MemberMapper mapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public Member signup(Account account, Member member) throws Exception {


        FaceDetection temp= new FaceDetection();
        boolean flag=temp.send(account.toString(),new String());//가족 구분 path와 판단하고자 하는 이미지 경로를 넘겨주어야 함
        

        if (flag) {
            // 이미 등록이 된 가족의 얼굴 이라면 등록을 하지는 않을 것이다.
            throw new BaseException(ErrorMessage.EXIST_ID);
        }

        //등록될 회원의 경로명을 등록해 주는 부분입니다.
        member.setModel_path(new String());//나중에 경로명 지정하게 될시 설정 해주자.





        MemberMapper.signup(member);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String salt = member.getUid().toString() + calendar.getTime();

        salt = (BCrypt.hashpw(salt, BCrypt.gensalt()));// salt를 설정하는 부분은 무엇일까?
        userMapper.setSalt(userDTO.getUid(), salt);

        return userMapper.findUserById(userDTO.getUsername()).get();
    }



    @Override
    public Map<String, Object> login(Member loginDto) throws Exception {
        UserDTO userDto = userMapper.findUserById(loginDto.getId())//id를 기반으로 userDTO에 해당되는 정보를 찾음
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_ID));

        if (userDto.getLevel() == 0) {
            throw new BaseException(ErrorMessage.SIGNUP_LISTEN);
        }
        if (!passwordEncoder.matches(loginDto.getPw(), userDto.getPassword())) {//passwordEncoder를 활용하여 db에 저장된것과 일치하는 지 판단
            throw new BaseException(ErrorMessage.NOT_PASSWORD);
        }
        //존재하는 회원이고 비밀번호도 일치를 했다면, jwttokenprovider를 통해서 ,id와 role값을 전달하여 토큰을 생성후 프론트 단에 전달해줌
        String accessToken = jwtTokenProvider.createToken(userDto.getUid(), Collections.singletonList(userDto.getRole()));
        String refreshToken = jwtTokenProvider.createRefresh(userDto.getUid(), Collections.singletonList(userDto.getRole()));
        userDto.setRefresh_token(refreshToken);
        userMapper.setRefreshToken(userDto);
        return new HashMap<String, Object>() {{
            put("name", userDto.getName());
            put("access-token", accessToken);
            put("refresh-token", refreshToken);
            put("uid", userDto.getUid());
        }};
    }






    @Override
    public String refreshToken(Long uid, String token) throws Exception {
        Optional<UserDTO> object = userMapper.findUserByUid(uid);
        if (object.isPresent()) {
            UserDTO userDTO = object.get();
            if (token.equals(userDTO.getRefresh_token())) {
                if (jwtTokenProvider.validateToken(token))
                    return jwtTokenProvider.createToken(userDTO.getUid(), Collections.singletonList(userDTO.getRole()));
                else
                    throw new BaseException(ErrorMessage.ACCESS_TOKEN_EXPIRE);
            } else {
                throw new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH);
            }
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }
    }






//    @Override
//    public void sendSignupEmail(UserDTO user) throws Exception {
//        user = userMapper.findUserById(user.getId()).get();
//        if (user.getLevel() != 0)
//            throw new BaseException(ErrorMessage.EXIST_CHECK_MAIL);
//        String token = jwtTokenProvider.create(user.getUid(), Collections.singletonList(user.getRole()), 1000 * 60 * 30);
//        emailHandler.sendMail(user.getEmail(), "Comunit 이메일 인증입니다.", "<h1>Comunit 이메일 인증 회원가입이예요</h1><a href='http://183.97.128.216/check?token=" + token + "'>여기를 눌러 인증해주세요.</a>", true);
//    }
//
//    @Override
//    public void resendCheckMail(LoginDTO loginDTO) throws Exception {
//        UserDTO user = userMapper.findUserById(loginDTO.getId())
//                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_ID));
//
//        if (passwordEncoder.matches(loginDTO.getPw(), user.getPassword())) {
//            sendSignupEmail(user);
//        } else {
//            throw new BaseException(ErrorMessage.NOT_PASSWORD);
//        }
//    }
//
//    @Override
//    public void checkEmail(String token) throws Exception {
//        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//            Long uid = Long.parseLong(jwtTokenProvider.getUserId(token));
//            UserDTO user = userMapper.findUserByUid(uid).get();
//            if (user.getLevel() != 0)
//                throw new BaseException(ErrorMessage.EXIST_CHECK_MAIL);
//            userMapper.checkEmail(uid);
//        } else {
//            throw new BaseException(ErrorMessage.ACCESS_TOKEN_INVALID);
//        }
//    }

//    @Override
//    public void findMyPW(UserDTO user) throws Exception {
//        UserDTO suser = userMapper.findUserByEmail(user.getEmail()).get();
//
//        if (suser != null) {
//            if (suser.getId().equals(user.getId()) && suser.getName().equals(user.getName())) {
//                Random rnd = new Random();
//                StringBuilder temp_pw = new StringBuilder();
//                for (int i = 0; i < 20; i++) {
//                    if (rnd.nextBoolean()) {
//                        temp_pw.append((char) ((int) (rnd.nextInt(26)) + 97));
//                    } else {
//                        temp_pw.append((rnd.nextInt(10)));
//
//                    }
//                }
//                String epw = passwordEncoder.encode(temp_pw);
//                userMapper.setPassword(user.getEmail(), epw);
//
//                emailHandler.sendMail(user.getEmail(), "임시 비밀번호입니다.", "임시 비밀번호는 " + temp_pw + " 입니다.", false);
//            } else {
//                throw new BaseException(ErrorMessage.NOT_USER_INFO_MATCH);
//            }
//        } else {
//            throw new BaseException(ErrorMessage.NOT_USER_INFO);
//        }
//    }





}
