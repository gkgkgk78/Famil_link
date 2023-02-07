package com.famillink.controller;


import com.famillink.model.domain.param.PhotoSenderDTO;
import com.famillink.model.service.PhotoService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Api("Account Controller")
@RequiredArgsConstructor
@RequestMapping("/account")
@JsonAutoDetect
@RestController
public class PhotoController {

    private final PhotoService photoService;

    //프사 등록을 위한 부분

    @PostMapping("/photo")
    @ApiOperation(value = "개인 멤버 사진 보내기", notes = "req_data : [token, img file, 보내는 사람 uid]")
    public ResponseEntity<?> addPhoto(@RequestBody PhotoSenderDTO sender, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
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

        photoService.delete(name, authentication);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "개인 멤버 삭제 완료");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
