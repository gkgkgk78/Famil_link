package com.famillink.controller;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.service.FileService;
import com.famillink.model.service.FlaskService;
import com.famillink.model.service.MovieService;
import com.famillink.model.service.ToFlask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/flask")
@RestController
@Api("flask controller")
public class FlaskController {


    private final ToFlask toFlask;

    private final FlaskService flaskService;

    @PostMapping("/model")
    @ApiOperation(value = "flask로 학습된 model 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addModel(@RequestBody Long uid) throws Exception {
        toFlask.send(uid, "model");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @PostMapping("/label")
    @ApiOperation(value = "flask로 학습된 label 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addLabel(@RequestBody Long uid) throws Exception {
        toFlask.send(uid, "label");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @PostMapping("/model")
    @ApiOperation(value = "Flask 모델 저장 ", notes = "req_data : [token, flask 파일]")
    public ResponseEntity<?> addModel(final Authentication authentication, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {



        Account auth = (Account) authentication.getPrincipal();
        flaskService.send_model(auth, file);
        return null;
    }

    @GetMapping("/model")
    @ApiOperation(value = "Flask의 Model 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> returnModel(final Authentication authentication) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        if (account.getEmail() == null)
            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);

        InputStreamResource resource = flaskService.read_model(account.getEmail());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=model.h5").body(resource);

    }


    @PostMapping("/label")
    @ApiOperation(value = "Flask Label 저장 ", notes = "req_data : [token, label 파일]")
    public ResponseEntity<?> addLabel(final Authentication authentication, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        flaskService.send_label(account, file);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "label저장 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @GetMapping("/label")
    @ApiOperation(value = "Flask의 Label 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> returnLabel(final Authentication authentication) throws Exception {
        Account account = (Account) authentication.getPrincipal();
        if (account.getEmail() == null)
            throw new BaseException(ErrorMessage.NOT_EXIST_EMAIL);

        InputStreamResource resource = flaskService.read_label(account.getEmail());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labels.txt").body(resource);

    }


}
