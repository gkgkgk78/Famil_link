package com.famillink.controller;

import com.famillink.model.service.FlaskService;
import com.famillink.model.service.ToFlask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/flask")
@RestController
@Api("flask controller")
public class FlaskController {


    private final ToFlask toFlask;

    private final FlaskService flaskService;

    @PostMapping("/learn_model")
    @ApiOperation(value = "flask로 학습된 model 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addModel(@RequestParam Long uid) throws Exception {
        toFlask.send(uid, "model");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @PostMapping("/learn_label")
    @ApiOperation(value = "flask로 학습된 label 보내기", notes = "req data:[token]")
    public ResponseEntity<?> addLabel(@RequestParam Long uid) throws Exception {
        toFlask.send(uid, "label");
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @PostMapping("/model")
    @ApiOperation(value = "Flask 모델 저장 ", notes = "req_data : [token, flask 파일]")
    public ResponseEntity<?> addModel(@RequestParam Long uid, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {

        flaskService.send_model(uid, file);
        return null;
    }

    @GetMapping("/model")
    @ApiOperation(value = "Flask의 Model 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> returnModel(@RequestParam Long uid) throws Exception {

        InputStreamResource resource = flaskService.read_model(uid);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=model.h5").body(resource);
    }


    @PostMapping("/label")
    @ApiOperation(value = "Flask Label 저장 ", notes = "req_data : [token, label 파일]")
    public ResponseEntity<?> addLabel(@RequestParam Long uid, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {

        flaskService.send_label(uid, file);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "label저장 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @GetMapping("/label")
    @ApiOperation(value = "Flask의 Label 불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> returnLabel(@RequestParam Long uid) throws Exception {

        InputStreamResource resource = flaskService.read_label(uid);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).cacheControl(CacheControl.noCache()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labels.txt").body(resource);

    }


}
