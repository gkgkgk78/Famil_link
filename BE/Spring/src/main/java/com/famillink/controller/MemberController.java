package com.famillink.controller;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieDTO;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.param.ImageDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.service.*;
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
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Api("Member Controller")
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController

public class MemberController {
    private final MemberService memberservice;

    private final FaceDetection fservice;


    private final MovieService movieService;

    private final ToFlask toFlask;


    @ApiOperation(value = "회원가입", notes = "req_data : [name,nickname]")
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


    //웹용 로그인
    @ApiOperation(value = "개인멤버 로그인", notes = "req_data : [image file,uid]")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ImageDTO imageDTO, final Authentication authentication) throws Exception {

        //flask에 학습된 모델과 labels를 보내는 과정임
        toFlask.send(authentication, "model");
        toFlask.send(authentication, "label");


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


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<String, Object>() {{
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


    @PostMapping("/movie")
    @ApiOperation(value = "동영상 보내기", notes = "req_data : [image,fromuid,touid]")
    public ResponseEntity<?> addMovie(MovieSenderDTO sender, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        movieService.sender(sender, file);
        return null;
    }

    @GetMapping("/movie/{movie_uid}")
    @ApiOperation(value = "동영상 보기", notes = "req_data : [token, movie uid]")
    public ResponseEntity<StreamingResponseBody> getMovie(@PathVariable("movie_uid") Long movie_uid, Authentication authentication) throws Exception {
        final HttpHeaders responseHeaders = new HttpHeaders();

        // TODO: Service단에서 http 관련 작업을 하면 안된다.
        StreamingResponseBody resource = movieService.download(movie_uid, responseHeaders, authentication);

        responseHeaders.add("Content-Type", "video/mp4");
        return ResponseEntity.ok().headers(responseHeaders).body(resource);
    }


    @PutMapping("/movie/{movie_uid}")
    @ApiOperation(value = "동영상 읽음 처리", notes = "req_data : [movieuid,token]")
    public ResponseEntity<?> setMovie(@PathVariable("movie_uid") Long movie_uid) throws Exception {


        movieService.setRead(movie_uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "동영상 읽음처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @GetMapping("/video-list/{member_to}")
    @ApiOperation(value = "동영상 리스트 전송", notes = "이 컨트롤러는 최신 영상 5개를 담은 동영상 리스트를 전송합니다.")
    public ResponseEntity<?> sendList(@PathVariable("member_to") Long member_to) throws Exception {

        List<MovieDTO> movieList = movieService.showMovieList(member_to);

        Map<String, Object> responseResult = new HashMap<>();

        if (movieList.isEmpty()) {
            responseResult.put("msg", "도착한 영상이 없습니다");
            return ResponseEntity.status(HttpStatus.OK).body(responseResult);
        }

        responseResult.put("movie-list", movieList);
        responseResult.put("msg", "최근 수신된 영상 리스트입니다");

        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


        //TODO: 파일 다운로드 test
//    @PostMapping( "/regist-member/{name}")
//    @ApiOperation(value = "멤버 등록 영상입니다", notes = "이 컨트롤러는 멤버 등록을 위한 영상을 받는 컨트롤러입니다.")
//    public ResponseEntity<?> registMember(Authentication authentication, @PathVariable String name, @RequestPart(value = "file", required = true) MultipartFile file) throws FileNotFoundException {
//
//        Account account = (Account) authentication.getPrincipal();
//
//
//        String date = LocalDate.now().format((DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String videoName = date + "_" + account.getUsername() + "_" + name;
//
//
//
//    }

}
