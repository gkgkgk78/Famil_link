package com.famillink.controller;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.param.ImageDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.service.FaceDetection;
import com.famillink.model.service.FlaskService;
import com.famillink.model.service.MemberService;
import com.famillink.model.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api("Member Controller")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController

public class MemberController {
    private final MemberService memberservice;

    private final FaceDetection fservice;

    private final FlaskService flaskService;

    private final MovieService movieService;


    @ApiOperation(value = "회원가입", notes = "req_data : [model_path,name,nickname,user_uid]")
    @PostMapping("/signup/{name}/{nickname}")

    public ResponseEntity<?> signup(@PathVariable String name, @PathVariable String nickname, final Authentication authentication) throws Exception {

        //우선은 온 파일의 정보를 임시로 저장을 해두면 될듯 하다.

//        String temp = flaskService.send_temp(account, file);
//        long flag = fservice.isCongnitive("", temp);
//        flaskService.delete_temp(temp);
//
//        if (flag == 0) {
//            throw new BaseException(ErrorMessage.NOT_USER_INFO);
//        }

        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();

        //회원가입을 할시에 자신이 찍은 사진을 바탕으로 회원가입이 되는 여부를 판단을 할수 있음
        Member savedUser = memberservice.signup(name, nickname, tt);
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "멤버 가입 성공");
        }}, HttpStatus.OK);
        
        
        
    }


    @ApiOperation(value = "개인멤버 로그인", notes = "req_data : [id, pw]")
    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody ImageDTO imageDTO, final Authentication authentication) throws Exception {



        //안면인식으로 추출한 멤버
        String member_name = fservice.getMemberUidByFace(imageDTO.getJson());
        if (member_name.equals("NONE")) {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }

        //uid로 추출한 멤버
        Member member = memberservice.findMemberByUserUid(imageDTO.getUid()).get();


        //두 멤버의 이름이 일치하면
        if(member.getName().equals(member_name)){

            Long member_uid = memberservice.findByUserName(member_name);

            //임시로 uid8로 넣은후 인증 되는지 확인(download시)
            Map<String, Object> token = memberservice.login(member_uid);

            return new ResponseEntity<Object>(new HashMap<String, Object>() {{
                put("result", true);
                put("msg", "로그인을 성공하였습니다.");
                put("access-token", token.get("access-token"));
                put("refresh-token", token.get("refresh-token"));
                put("uid", token.get("uid"));
                put("name", token.get("name"));
            }}, HttpStatus.OK);

        }


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<String, Object>(){{
            put("result", false);
            put("msg", "로그인 시도자와 멤버 정보가 일치하지 않습니다");
        }});

    }

    @ApiOperation(value = "Member Access Token 재발급", notes = "만료된 access token을 재발급받는다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Long uid, HttpServletRequest request) throws Exception {
        HttpStatus status = HttpStatus.ACCEPTED;
        String token = request.getHeader("refresh-token");
        String result = memberservice.refreshToken(uid, token);
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


    @ApiOperation(value = "Member회원 확인", notes = "회원정보를 반환합니다.")
    @GetMapping("/auth")
    public ResponseEntity<?> authUser(final Authentication authentication) {
        Member auth = (Member) authentication.getPrincipal();
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("data", auth);
        }}, HttpStatus.OK);
    }


}
