package com.famillink.controller;

import com.famillink.model.domain.param.MovieDTO;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.service.FileService;
import com.famillink.model.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/movie")
@RestController
@Api("movie controller")
public class MovieController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final MovieService movieService;

    private final FileService fileService;

    @PostMapping("/")
    @ApiOperation(value = "동영상 보내기", notes = "req_data : [image,fromuid,touid]")
    public ResponseEntity<?> addMovie( MovieSenderDTO sender, @RequestPart(value = "mp4", required = true) MultipartFile file) throws Exception {
        movieService.sender(sender, file);
        return null;
    }

    @GetMapping("/{movie_uid}")
    @ApiOperation(value = "동영상 보기", notes = "req_data : [token, movie uid]")
    public ResponseEntity<StreamingResponseBody> getMovie(@PathVariable("movie_uid") Long movie_uid, Authentication authentication) throws Exception {
        final HttpHeaders responseHeaders = new HttpHeaders();

        // TODO: Service단에서 http 관련 작업을 하면 안된다.
        StreamingResponseBody resource = movieService.download(movie_uid, responseHeaders, authentication);
        responseHeaders.add("Content-Type", "video/mp4");
        return ResponseEntity.ok().headers(responseHeaders).body(resource);
    }

    @PutMapping("/{movie_uid}")
    @ApiOperation(value = "동영상 읽음 처리", notes = "req_data : [movieuid,token]")
    public ResponseEntity<?> setMovie(@PathVariable("movie_uid") Long movie_uid) throws Exception {


        movieService.setRead(movie_uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "동영상 읽음처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }

    @GetMapping("/video-list")
    @ApiOperation(value = "동영상 리스트 전송", notes = "이 컨트롤러는 최신 영상 5개를 담은 동영상 리스트를 전송합니다.")
    public ResponseEntity<?> sendList(Authentication authentication) throws Exception {

        Member member = (Member) authentication.getPrincipal();

        Long member_to = member.getUser_uid();

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

    @PostMapping(value = "/member", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "멤버 등록 영상 저장", notes = "이 컨트롤러는 멤버 등록 영상을 저장합니다.")
    public ResponseEntity<?> registMember(@RequestPart("name") String name ,@RequestPart("file") MultipartFile file, Authentication authentication) throws IOException {

        Account account = (Account) authentication.getPrincipal();

        //test
        // String path = fileService.saveRegistVideo(file, name, 6L);

        String path = fileService.saveRegistVideo(file, name, account.getUid());

        return ResponseEntity.status(HttpStatus.OK).body(path);
    }

    @DeleteMapping("/regist/{fileName}")
    @ApiOperation(value = "멤버 등록 영상 삭제", notes = "관리자가 파일을 삭제하는 컨트롤러 입니다")
    public ResponseEntity<?> deleteRegistVideo(@PathVariable String fileName) throws Exception {

        fileService.deleteFile(fileName);

        return ResponseEntity.status(HttpStatus.OK).body("delete OK");
    }


}
