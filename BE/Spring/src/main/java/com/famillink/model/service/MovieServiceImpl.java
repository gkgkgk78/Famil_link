package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.model.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.nio.file.Files;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;
    private final FileService fileService;
    private final MovieMapper movieMapper;
    private final MemberMapper memberMapper;
    private final MemberService mservice;

    @Override
    @Transactional
    public void sender(MovieSenderDTO sender, MultipartFile file) throws Exception {

        //현재는 sender에 있는 보내고자 하는 얘들이 같은
        Member m;
        try {
            if (!mservice.findTogether(sender))//계정 정보가 일치 하지 않을시에 처리 하고자 하는 상황
            {
                throw new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO);
            }
        } catch (Exception e) {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }

        m = memberMapper.findUserByUid(sender.getFrom_member_uid()).get();
        //가족 uid로 폴더에 저장을 해줌
        String get = fileService.store(file, m.getUser_uid());
        movieMapper.sendMovie(sender, get);

    }

    @Override
    public StreamingResponseBody download(Long movie_uid, HttpHeaders httpHeaders) throws Exception {

        try {
            //Movie table에서 보낸자와 받은 자가 같은 가족인지 판단을 해서 처리를 함
            MovieSenderDTO sender = movieMapper.getMovie(movie_uid);
            if (!mservice.findTogether(sender))//계정 정보가 일치 하지 않을시에 처리 하고자 하는 상황
            {
                throw new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO);
            }
        } catch (Exception e) {
            //System.out.println(e);
            throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
        }
        String filename = movieMapper.getMoviePath(movie_uid);
        filename = "./" + filename;
        File file = new File(filename);
        if (!file.isFile()) {
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }
        httpHeaders.add("Content-Length", Long.toString(file.length()));

        // 파일 리소스 리턴
        return outputStream -> {
            FileCopyUtils.copy(Files.newInputStream(file.toPath()), outputStream);
        };
    }


    @Override
    public void setRead(Long movie_uid) throws Exception {

        try {
            movieMapper.setMovie(movie_uid);
        } catch (Exception e) {
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }
    }

    @Override
    public List<MovieSenderDTO> showMovieList(Long to_member_uid) throws Exception {
        return movieMapper.findMovieByMemberTo(to_member_uid);
    }


}
