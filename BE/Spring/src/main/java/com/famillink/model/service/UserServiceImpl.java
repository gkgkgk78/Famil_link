package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.UserDTO;
import com.famillink.model.domain.param.LoginDTO;
import com.famillink.model.mapper.UserMapper;
import com.famillink.util.EmailHandler;
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
public class UserServiceImpl implements UserService {
    private final EmailHandler emailHandler;
    private final UserMapper userMapper;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDTO signup(UserDTO userDTO) throws Exception {
        if (userMapper.findUserById(userDTO.getId()).isPresent()) {
            // 이미 존재하는 아이디
            throw new BaseException(ErrorMessage.EXIST_ID);
        }

        if (userMapper.findUserByNickname(userDTO.getNickname()).isPresent()) {
            throw new BaseException(ErrorMessage.EXIST_NICKNAME);
        }

        if (userMapper.findUserByEmail(userDTO.getEmail()).isPresent()) {
            // 이미 존재하는 이메일
            throw new BaseException(ErrorMessage.EXIST_EMAIL);
        }
        
        //권한 부분은 현재 코드 상으로 userDTO에 이미 지정이 되어 있어서 따로 parameter로 오지 않은것 같음
        //해당 부분은 로그인시 가족계정, 개인 계정 으로 권한이 분리 되어서 접근이 되는곳이 생길 경우, 프론트 단에서 넘겨줄때 고려해서 넘겨 줘야 할것으로 생각
        
        userDTO.setPw(passwordEncoder.encode(userDTO.getPassword()));//회원가입시 비밀번호는 암호화 해서 저장해야 하기에 이렇게 지정함
        userMapper.signup(userDTO);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String salt = userDTO.getUid().toString() + calendar.getTime();

        salt = (BCrypt.hashpw(salt, BCrypt.gensalt()));// salt를 설정하는 부분은 무엇일까?
        userMapper.setSalt(userDTO.getUid(), salt);

        return userMapper.findUserById(userDTO.getUsername()).get();
    }

    @Override
    public Map<String, Object> login(LoginDTO loginDto) throws Exception {
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

    @Override
    public void sendSignupEmail(UserDTO user) throws Exception {
        user = userMapper.findUserById(user.getId()).get();
        if (user.getLevel() != 0)
            throw new BaseException(ErrorMessage.EXIST_CHECK_MAIL);
        String token = jwtTokenProvider.create(user.getUid(), Collections.singletonList(user.getRole()), 1000 * 60 * 30);
        emailHandler.sendMail(user.getEmail(), "Comunit 이메일 인증입니다.", "<h1>Comunit 이메일 인증 회원가입이예요</h1><a href='http://183.97.128.216/check?token=" + token + "'>여기를 눌러 인증해주세요.</a>", true);
    }

    @Override
    public void resendCheckMail(LoginDTO loginDTO) throws Exception {
        UserDTO user = userMapper.findUserById(loginDTO.getId())
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_ID));

        if (passwordEncoder.matches(loginDTO.getPw(), user.getPassword())) {
            sendSignupEmail(user);
        } else {
            throw new BaseException(ErrorMessage.NOT_PASSWORD);
        }
    }

    @Override
    public void checkEmail(String token) throws Exception {
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            Long uid = Long.parseLong(jwtTokenProvider.getUserId(token));
            UserDTO user = userMapper.findUserByUid(uid).get();
            if (user.getLevel() != 0)
                throw new BaseException(ErrorMessage.EXIST_CHECK_MAIL);
            userMapper.checkEmail(uid);
        } else {
            throw new BaseException(ErrorMessage.ACCESS_TOKEN_INVALID);
        }
    }

    @Override
    public void findMyPW(UserDTO user) throws Exception {
        UserDTO suser = userMapper.findUserByEmail(user.getEmail()).get();

        if (suser != null) {
            if (suser.getId().equals(user.getId()) && suser.getName().equals(user.getName())) {
                Random rnd = new Random();
                StringBuilder temp_pw = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    if (rnd.nextBoolean()) {
                        temp_pw.append((char) ((int) (rnd.nextInt(26)) + 97));
                    } else {
                        temp_pw.append((rnd.nextInt(10)));

                    }
                }
                String epw = passwordEncoder.encode(temp_pw);
                userMapper.setPassword(user.getEmail(), epw);

                emailHandler.sendMail(user.getEmail(), "임시 비밀번호입니다.", "임시 비밀번호는 " + temp_pw + " 입니다.", false);
            } else {
                throw new BaseException(ErrorMessage.NOT_USER_INFO_MATCH);
            }
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }
    }
}
