package com.famillink.controller;


import com.famillink.annotation.ValidationGroups;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.PhotoSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.domain.user.Todo;
import com.famillink.model.service.AccountService;
import com.famillink.model.service.FlaskService;
import com.famillink.model.service.PhotoService;
import com.famillink.model.service.TodoService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api("Account Controller")
@RequiredArgsConstructor
@RequestMapping("/account")
@JsonAutoDetect
@RestController
public class AccountController {
    private final AccountService accountService;
    private final FlaskService flaskService;

    private final TodoService todoService;

    private final PhotoService photoService;

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
    public ResponseEntity<?> loginUser(@RequestBody @Validated(ValidationGroups.signup.class) Account account) throws Exception {

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

        if (members.isEmpty()){
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(members);
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
    public ResponseEntity<?> resendCheckMail(@RequestBody @Validated(ValidationGroups.signup.class) Account loginAccount) throws Exception {
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


    @PostMapping("/flask/model")
    @ApiOperation(value = "Flask 모델 저장 ", notes = "req_data : [token, flask 파일]")
    public ResponseEntity<?> addModel(final Authentication authentication, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        Account auth = (Account) authentication.getPrincipal();

        flaskService.send_model(auth, file);
        return null;
    }

    @GetMapping("/flask/model")
    @ApiOperation(value = "Flask의 Model 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> returnModel(final Authentication authentication) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        if (account.getEmail() == null)
            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);

        InputStreamResource resource = flaskService.read_model(account.getEmail());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=model.h5").body(resource);

    }


    @PostMapping("/flask/label")
    @ApiOperation(value = "Flask Label 저장 ", notes = "req_data : [token, label 파일]")
    public ResponseEntity<?> addLabel(final Authentication authentication, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        flaskService.send_label(account, file);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "label저장 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @GetMapping("/flask/label")
    @ApiOperation(value = "Flask의 Label 불러오기", notes ="req_data : [token]")
    public ResponseEntity<?> returnLabel(final Authentication authentication) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        if (account.getEmail() == null)
            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);

        InputStreamResource resource = flaskService.read_label(account.getEmail());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labels.txt").body(resource);

    }

    @PostMapping("/todo/{content}")
    @ApiOperation(value = "가족 todo생성", notes = "req_data : [token, 내용]")
    public ResponseEntity<?> AddTodo(Authentication authentication, @PathVariable String content) throws Exception {
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        todoService.createtodo(tt, content);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo생성 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @GetMapping("/todo")
    @ApiOperation(value = "가족 todo불러오기", notes = "req_data : [token, flask 파일]")
    public ResponseEntity<?> GetTodo(Authentication authentication) throws Exception {
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        List<Todo> list = todoService.gettodo(tt);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo불러오기 성공");
        responseResult.put("todolist", list);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @DeleteMapping("/todo/{uid}")
    @ApiOperation(value = "가족 todo삭제하기", notes = "req_data : [token]")
    public ResponseEntity<?> DeleteTodo(@PathVariable Long uid) throws Exception {
        todoService.deletetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo삭제 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @PutMapping("/todo/{uid}")
    @ApiOperation(value = "가족 todo완료처리", notes = "req_data : [token,가족 uid]")
    public ResponseEntity<?> CompleteTodo(@PathVariable Long uid) throws Exception {
        todoService.updatetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo완료처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    //프사 등록을 위한 부분

    @PostMapping("/photo/{name}")
    @ApiOperation(value = "개인 멤버 사진 보내기", notes = "req_data : [token, img file, 보내는 사람 uid, 받는 사람이름]")
    public ResponseEntity<?> addPhoto(PhotoSenderDTO sender, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        photoService.sender(sender, file);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "개인 멤버 사진 완료");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    //프사 넘겨줌
    @GetMapping("/photo/{name}}")
    @ApiOperation(value = "개인 멤버 사진 보내기 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> getPhoto(@PathVariable String name, final Authentication authentication) throws Exception {

        InputStreamResource resource = photoService.download(name, authentication);
        String filename = name + ".jpg";
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename).body(resource);

    }
    
    @DeleteMapping("/photo/{name}}")
    @ApiOperation(value = "개인 멤버 사진 삭제하기", notes = "req_data : [name,token]")
    public ResponseEntity<?> deletePhoto(@PathVariable String name, final Authentication authentication) throws Exception {

        photoService.delete(name,authentication);

        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "개인 멤버 삭제 완료");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
