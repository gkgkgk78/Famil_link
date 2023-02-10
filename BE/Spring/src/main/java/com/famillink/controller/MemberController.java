package com.famillink.controller;

import com.famillink.annotation.ValidationGroups;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.ImageDTO;
import com.famillink.model.domain.param.PhotoSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.domain.user.Member_Login;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.model.service.FaceDetection;
import com.famillink.model.service.MemberService;
import com.famillink.model.service.PhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Api("Member Controller")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController

public class MemberController {
    private final MemberService memberservice;

    private final FaceDetection fservice;

    private final MemberMapper mapper;


    private final PhotoService photoService;

    @ApiOperation(value = "회원가입", notes = "req_data : [name,nickname,account_token]")
    //@PostMapping("/signup")
    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> signup(@ModelAttribute  Member_Login member, @RequestPart(value = "img", required = true) MultipartFile file, final Authentication authentication) throws Exception {


        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();


        Member savedUser = memberservice.signup(member.getName(), member.getNickname(), tt);
        PhotoSenderDTO sender=new PhotoSenderDTO(auth.getUid(),member.getName());
        photoService.sender(sender, file);
        return new ResponseEntity<Object>(new HashMap<String, Object>() {{
            put("result", true);
            put("msg", "멤버 가입과 사진등록 성공");
        }}, HttpStatus.OK);


    }


    @ApiOperation(value = "개인멤버 로그인", notes = "req_data : [image file,uid]")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated(ValidationGroups.member_login.class) ImageDTO imageDTO, final Authentication authentication) throws Exception {


        //안면인식으로 추출한 멤버
        String member_name = fservice.getMemberUidByFace(imageDTO.getJson(), authentication);
        if (member_name.equals("NONE")) {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);

        }

        //멤버 정보는 존재를 했지만 찾아온 멤버 정보가 지금 로그인한 가족 계정에 속하는지 아닌지를 판단을 해야함
        //즉, 판단한 얼굴 정보 <=> 로그인한 token이 찾은 얼굴에 속하는지 판단
        Member member = memberservice.findMemberByUserUid(imageDTO.getUid()).get();//uid로 추출한 멤버
        Account account = (Account) authentication.getPrincipal();
        if (account.getUid() != member.getUser_uid())
            throw new BaseException(ErrorMessage.NOT_MATCH_FAMILY);


        //두 멤버의 이름이 일치하면
        if (member.getName().equals(member_name)) {

            //하나 만들고자 함
            //여기서 하나 만 찾아 내야 하는데
            Map<String, Object> map = new HashMap<>();
            map.put("user_uid", account.getUid());
            map.put("name", member_name);
            Optional<Member> temp = mapper.findUserByNametoAll(map);
            Member m1 = null;
            if (temp.isPresent()) {
                m1 = temp.get();
            } else {
                throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
            }


            Long member_uid = m1.getUid();


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

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<String, Object>() {{
            put("result", false);
            put("msg", "로그인 시도자와 멤버 정보가 일치하지 않습니다");
        }});

    }

    @ApiOperation(value = "Member Access Token 재발급", notes = "만료된 access token을 재발급받는다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam Long uid, HttpServletRequest request) throws Exception {
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
