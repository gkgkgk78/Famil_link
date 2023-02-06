package com.famillink.controller;

import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.service.FileService;
import com.famillink.model.service.MovieService;
import com.famillink.model.service.ToFlask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/model")
    @ApiOperation(value = "flask로 학습된 model 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addModel(Authentication authentication) throws Exception {
        toFlask.send(authentication, "model");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);

        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @PostMapping("/label")
    @ApiOperation(value = "flask로 학습된 label 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addLabel(Authentication authentication) throws Exception {
        toFlask.send(authentication, "label");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "도착한 영상이 없습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
