package com.famillink.controller;

import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/movie")
@RestController
@Api("movie controller")
public class MovieController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    @PostMapping("/movie")
    @ApiOperation(value = "동영상 보내기", notes = "동영상을 전송하는 컨트롤러입니다.")
    public ResponseEntity<?> addMovie(MovieSenderDTO sender, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        movieService.sender(sender, file);
        return null;
    }

    @GetMapping("/movie/{movie_uid}")
    @ApiOperation(value = "동영상 보기", notes = "동영상을 다운받는 컨트롤러입니다.")
    public ResponseEntity<StreamingResponseBody> getMovie(@PathVariable("movie_uid") Long movie_uid,Authentication authentication) throws Exception {
        final HttpHeaders responseHeaders = new HttpHeaders();

        // TODO: Service단에서 http 관련 작업을 하면 안된다.
        StreamingResponseBody resource = movieService.download(movie_uid, responseHeaders, authentication);

        responseHeaders.add("Content-Type", "video/mp4");
        return ResponseEntity.ok().headers(responseHeaders).body(resource);
    }


    @PutMapping("/movie/{movie_uid}")
    @ApiOperation(value = "동영상 읽음 처리", notes = "동영상 읽음 처리를 위한 컨트롤러입니다.")
    public ResponseEntity<?> setMovie(@PathVariable("movie_uid") Long movie_uid) throws Exception {


        movieService.setRead(movie_uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "동영상 읽음처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
