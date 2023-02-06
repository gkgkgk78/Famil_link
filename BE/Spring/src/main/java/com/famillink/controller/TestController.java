package com.famillink.controller;

import com.famillink.model.domain.user.Account;
import com.famillink.model.service.FlaskService;
import com.famillink.model.service.TestService;
import com.famillink.model.service.ToFlask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.nio.file.Files;


@Api("Test Controller")
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    private final TestService testService;
    private final FlaskService flaskService;

    private final ToFlask toFlask;

    @ApiOperation(value = "테스트", notes = "테스트 컨트롤러입니다.")
    @GetMapping("/test")
    public ResponseEntity<?> test(
            final Authentication authentication) {

        return new ResponseEntity<Object>("dd", HttpStatus.OK);
    }

    @ApiOperation(value = "파일 업로드", notes = "파일을 업로드합니다.")
    @PostMapping("/upload")
    public ResponseEntity<?> fileUpload(final Authentication authentication,
                                        @RequestPart(value = "imgUrlBase", required = true) MultipartFile imgUrlBase) throws Exception {
        logger.debug("MultipartFile.isEmpty : {}", imgUrlBase.isEmpty());
        testService.store(imgUrlBase);
        return null;
    }

    @ApiOperation(value = "파일 다운로드")
    @GetMapping("/download")
    public ResponseEntity<?> fileDownload(final Authentication authentication,
                                          @RequestParam(value = "filename") String filename) {

        Resource file = testService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ApiOperation(value = "동영상 스트리밍")
    @GetMapping(path = "/streaming")
    public ResponseEntity<StreamingResponseBody> movieStreaming(
            @RequestParam("file") String fileName,
            @RequestHeader HttpHeaders headers) throws Exception {
//        return new InputStreamResource(new FileInputStream("upfiles\\record.mp4"));
        // ResponseEntity<StreamingResponseBody>
        File file = new File("upfiles\\" + fileName);
        if (!file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody streamingResponseBody = outputStream -> {
            FileCopyUtils.copy(Files.newInputStream(file.toPath()), outputStream);
            System.out.println("dd");
        };
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "video/avi");
        responseHeaders.add("Content-Length", Long.toString(file.length()));
        return ResponseEntity.ok().headers(responseHeaders).body(streamingResponseBody);
    }

    @ApiOperation(value = "얼굴인식 파일저장")
    @PostMapping(path = "/facetemp")
    public ResponseEntity<?> facetemp(Account account, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {

        //우선은 온 파일의 정보를 임시로 저장을 해두면 될듯 하다.

        String temp = flaskService.send_temp(account, file);
        System.out.println(temp);

        return null;
    }


    @GetMapping("/toflask/")
    @ApiOperation(value = "flask로 임시 데이터 보내기", notes = "아아아아아아")
    public ResponseEntity<StreamingResponseBody> getMovie(Long uid) throws Exception {


        //한번은 label을 전송해야 하고
        toFlask.send(uid, "model");
        toFlask.send(uid, "label");
        //한번은 model을 전송해야함
        return null;
    }


//    @ApiOperation(value = "얼굴인식 파일삭제")
//    @PutMapping(path = "/facetemp")
//    public ResponseEntity<?> facetemp1(String path) throws Exception
//    {
//
//        //우선은 온 파일의 정보를 임시로 저장을 해두면 될듯 하다.
//
//        flaskService.delete_temp(path);
//
//
//        return null;
//    }

}
