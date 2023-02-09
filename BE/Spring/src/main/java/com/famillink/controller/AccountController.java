package com.famillink.controller;


import com.famillink.annotation.ValidationGroups;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.service.AccountService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api("Account Controller")
@RequiredArgsConstructor
@RequestMapping("/account")
@JsonAutoDetect
@RestController
public class AccountController {


    private final AccountService accountService;

    @ApiOperation(value = "회원가입", notes = "req_data : [pw, email, name]")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Validated(ValidationGroups.signup.class) Account account) throws Exception {
        Account savedAccount = accountService.signup(account);

        //비동기 처리
        accountService.sendSignupEmail(savedAccount);

        Map<String, Object> result = new HashMap<>();
        HttpStatus sts = HttpStatus.UNAUTHORIZED;

        if (savedAccount != null) {
            result.put("result", true);
            result.put("msg", "회원가입을 성공하였습니다.\\n이메일을 확인해주세요.\\n30분 이내 인증을 완료하셔야합니다.");
            //얼굴인식 파일 가져와서 보내주기


            //Http 응답 결과 변경
            sts = HttpStatus.OK;
        } else {
            result.put("result", false);
            result.put("msg", "회원가입을 실패했습니다");
        }


        //결과 수정 (true, false)
        return ResponseEntity.status(sts).body(result);
    }

    @ApiOperation(value = "로그인", notes = "req_data : [id, pw]")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Validated(ValidationGroups.login.class) Account account) throws Exception {

        Map<String, Object> token = accountService.login(account); //access token, refresh token

        Map<String, Object> responseResult = new HashMap<>();

        HttpStatus sts = HttpStatus.BAD_REQUEST;

        if (token != null) {
            sts = HttpStatus.OK;
            responseResult.put("result", true);
            responseResult.put("msg", "로그인을 성공하였습니다.");
            responseResult.put("access-token", token.get("access-token"));
            responseResult.put("refresh-token", token.get("refresh-token"));
            responseResult.put("uid", token.get("uid"));
            responseResult.put("nickname", token.get("nickname"));
        }

        return ResponseEntity.status(sts).body(responseResult);
    }

    @ApiOperation(value = "멤버 리스트 반환", notes = "로그인 후 요청 시, 해당 account에 속해 있는 멤버 리스트를 반환하는 컨트롤러 입니다")
    @GetMapping("/member-list")
    public ResponseEntity<?> getMembers(Authentication authentication) throws Exception {

        Account account = (Account) authentication.getPrincipal();

        List<Member> members = accountService.allMembers(account);

        if (members.isEmpty()) {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("result", true);
        response.put("list", members);
        response.put("msg", "멤버리스트입니다");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @ApiOperation(value = "Access Token 재발급", notes = "만료된 access token을 재발급받는다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Long uid, HttpServletRequest request) throws Exception {
        HttpStatus status = HttpStatus.ACCEPTED;
        String token = request.getHeader("refresh-token");
        String result = accountService.refreshToken(uid, token);
        if (result != null && !result.equals("")) {
            // 발급 성공
            return new ResponseEntity<Object>(new HashMap<String, Object>() {{
                put("result", true);
                put("msg", "토큰이 발급되었습니다.");
                put("access-token", result);
            }}, status);
        } else {
            // 발급 실패
            throw new RuntimeException("리프레시 토큰 발급에 실패하였습니다.");
        }
    }

    @ApiOperation(value = "인증 이메일 재발송", notes = "인증 메일을 재발송한다.")
    @PostMapping("/mail")
    public ResponseEntity<?> resendCheckMail(@RequestBody @Validated(ValidationGroups.mail.class) Account loginAccount) throws Exception {
        accountService.resendCheckMail(loginAccount);
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "이메일 재전송에 성공하였습니다.");
        }}, HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 인증 확인", notes = "회원가입 이메일 인증을 완료한다.")
    @GetMapping("/check/{token}")
    public ResponseEntity<?> checkSignup(@PathVariable("token") String token) throws Exception {
        accountService.checkEmail(token);
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "이메일 인증에 성공하였습니다.");
        }}, HttpStatus.OK);
    }


    @ApiOperation(value = "회원 확인", notes = "회원정보를 반환합니다.")
    @GetMapping("/auth")
    public ResponseEntity<?> authUser(final Authentication authentication) {
        Account auth = (Account) authentication.getPrincipal();

        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("data", auth);
        }}, HttpStatus.OK);
    }

    @ApiOperation(value = "비밀번호 찾기", notes = "회원의 임시 비밀번호를 메일로 전송합니다.")
    @PostMapping("/find/password")
    public ResponseEntity<?> findMyPW(@RequestBody @Validated(ValidationGroups.find_password.class) Account account) throws Exception {
        accountService.findMyPW(account);
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "이메일로 임시 비밀번호를 발급하였습니다.");
        }}, HttpStatus.OK);
    }


}
