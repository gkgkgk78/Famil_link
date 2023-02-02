package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.AccountMapper;
import com.famillink.util.EmailHandler;
import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final EmailHandler emailHandler;
    private final AccountMapper accountMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public Account signup(@RequestBody Account account) throws Exception {
        System.out.println(account.toString());
        if (accountMapper.findUserByEmail(account.getEmail()).isPresent()) {
            throw new BaseException(ErrorMessage.EXIST_EMAIL);
        }

        account.setPw(passwordEncoder.encode(account.getPassword()));
        accountMapper.signup(account);

        LocalDate date = LocalDate.now();
        String salt = account.getUid().toString() + date;

        salt = (BCrypt.hashpw(salt, BCrypt.gensalt()));
        accountMapper.setSalt(account.getUid(), salt);


        //추가적으로 가족 계정의 model_path를 넣어주고자함
        Account acc= accountMapper.findUserByEmail(account.getEmail()).get();
        String s1= String.valueOf(Paths.get("upfiles","Flask",acc.getUid().toString() ));
        String route="./"+s1;
        accountMapper.setModelPath(acc.getEmail(),s1,acc.getUid());

        return accountMapper.findUserByEmail(account.getEmail()).get();
    }


    @Override
    public Map<String, Object> login(Account loginAccount) throws Exception {
        Account account = accountMapper.findUserByEmail(loginAccount.getEmail())
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_EMAIL));

        if (account.getLevel() == 0) {
            throw new BaseException(ErrorMessage.SIGNUP_LISTEN);
        }

        if (!passwordEncoder.matches(loginAccount.getPw(), account.getPassword())) {
            throw new BaseException(ErrorMessage.NOT_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(account.getUid(), Collections.singletonList(account.getRole()));
        String refreshToken = jwtTokenProvider.createRefresh(account.getUid(), Collections.singletonList(account.getRole()));
        accountMapper.setRefreshToken(account);



        // 여기는 토큰 발행
        return new HashMap<String, Object>() {{
            put("access-token", accessToken);
            put("refresh-token", refreshToken);
            put("uid", account.getUid());
            put("nickname", account.getNickname());
        }};
    }

    @Override
    public List<Member> allMembers(Account loginAccount) throws Exception {

        List<Member> members = accountMapper.findUserFamily(loginAccount.getEmail());

        return members;
    }



    @Override
    public String refreshToken(Long uid, String token) throws Exception {
        Optional<Account> object = accountMapper.findUserByUid(uid);

        if (object.isPresent()) {
            Account account = object.get();
            if (token.equals(account.getRefresh_token())) {
                if (jwtTokenProvider.validateToken(token))
                    return jwtTokenProvider.createToken(account.getUid(), Collections.singletonList(account.getRole()));
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
    public void sendSignupEmail(Account account) throws Exception {
        account = accountMapper.findUserByEmail(account.getEmail()).get();

        if (account.getLevel() != 0) {
            throw new BaseException(ErrorMessage.EXIST_CHECK_MAIL);
        }

        //TODO; 참조 주소 변경

        String token = jwtTokenProvider.create(account.getUid(), Collections.singletonList(account.getRole()), 1000 * 60 * 30);
        emailHandler.sendMail(account.getEmail(), "Famillink 이메일 인증입니다", "<h1>Famillink 이메일 인증 회원가입 입니다.</h1><a href='http://i8a208.p.ssafy.io:3000/check?token=" + token + "'>여기를 눌러 인증해주세요.</a>", true);
    }

    @Override
    public void resendCheckMail(Account loginAccount) throws Exception {
        Account account = accountMapper.findUserByEmail(loginAccount.getEmail())
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_EMAIL));

        if (passwordEncoder.matches(loginAccount.getPw(), account.getPassword())) {
            sendSignupEmail(account);
        } else {
            throw new BaseException(ErrorMessage.NOT_PASSWORD);
        }
    }

    @Override
    public void checkEmail(String token) throws Exception {
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            Long uid = Long.parseLong(jwtTokenProvider.getUserId(token));
            Account account = accountMapper.findUserByUid(uid).get();

            if (account.getLevel() != 0)
                throw new BaseException(ErrorMessage.EXIST_EMAIL);
            accountMapper.checkEmail(uid);

        } else {
            throw new BaseException(ErrorMessage.ACCESS_TOKEN_INVALID);
        }

    }

    @Override
    public void findMyPW(Account account) throws Exception {
        Account saccount = accountMapper.findUserByEmail(account.getEmail()).get();

        if (saccount != null) {
            if (saccount.getEmail().equals(account.getEmail()) && saccount.getPhone().equals(account.getPhone())) {
                Random rmd = new Random();
                StringBuilder temp_pw = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    if (rmd.nextBoolean()) {
                        temp_pw.append((char) ((int) (rmd.nextInt(26)) + 97));
                    } else {
                        temp_pw.append((rmd.nextInt(10)));
                    }
                }

                String epw = passwordEncoder.encode(temp_pw);
                accountMapper.setPassword(account.getEmail(), epw);

                emailHandler.sendMail(account.getEmail(), "임시비밀번호입니다", "임시 비밀번호는 " + temp_pw + "입니다", false);


            } else {
                throw new BaseException(ErrorMessage.NOT_USER_INFO_MATCH);
            }
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }
    }
}