package com.famillink.controller;

import com.famillink.model.domain.user.Member;
import com.famillink.model.service.FaceDetection;
import com.famillink.model.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api("Member Controller")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController

public class MemberController {
    private final MemberService memberservice;

    private final FaceDetection fservice;

    @ApiOperation(value = "회원가입", notes = "req_data : [model_path,name,nickname,user_uid]")
    @PostMapping("/signup/{photo}")
    public ResponseEntity<?> signup(@RequestBody Member member, @PathVariable String photo) throws Exception {

        Member savedUser = memberservice.signup(member, photo);


        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "멤버 가입 성공");
        }}, HttpStatus.OK);

    }


    @ApiOperation(value = "개인멤버 로그인", notes = "req_data : [id, pw]")
    @PostMapping("/login/{photo}")

    public ResponseEntity<?> login(@RequestBody Member member, @PathVariable String photo) throws Exception {

        Map<String, Object> token = memberservice.login(member, photo);

        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "로그인을 성공하였습니다.");
            put("access-token", token.get("access-token"));
            put("refresh-token", token.get("refresh-token"));
            put("uid", token.get("uid"));
            put("name", token.get("name"));

        }}, HttpStatus.OK);
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


    //임시 테스트를 위해서 우선 이렇게 만들어 두었습니다.


    @ApiOperation(value = "사진보내기", notes = "제발.")
    @PostMapping("/pick")
    public ResponseEntity<?> picture() throws Exception {
        boolean response = fservice.isCongnitive("모자", "src/test/image/cjw.jpg");

        Map<String, Object> result = new HashMap<>();

        if(response){
            result.put("resul", "얼굴 인식이 성공했습니다");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            result.put("resul", "얼굴이 등록되지 않은 구성원입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

    }


}
