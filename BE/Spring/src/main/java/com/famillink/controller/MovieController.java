package com.famillink.controller;

import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.service.MovieService;
import com.famillink.model.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/movie")
@RestController
@Api("movie controller")
public class MovieController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    @PostMapping()
    @ApiOperation(value = "동영상 보내기", notes = "동영상을 전송하는 컨트롤러입니다.")
    public ResponseEntity<?> addMovie(MovieSenderDTO sender, @RequestPart(value = "imgUrlBase", required = true) MultipartFile file) throws Exception {
        movieService.sender(sender, file);
        return null;
    }
}
