package com.famillink.model.service;

import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;
    private final FileService fileService;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public void sender(MovieSenderDTO sender, MultipartFile file) throws Exception {

        // TODO: CJW, sender의 uid들이 같은 가족인지 valid 필요

        //현재는 sender에 있는 보내고자 하는 얘들이 같은

        movieMapper.family_validation(sender.getFrom_member_uid(),sender.getTo_member_uid());





        fileService.store(file);






        // TODO: CJW, file path를 가족마다 나누고 filename을 중복되지 않는 임의 값으로 변경해서 관리하면 편함.
        movieMapper.sendMovie(sender, moviePath + "/" + file.getOriginalFilename());




    }

    @Override
    public InputStreamResource download(Long movie_uid) throws Exception {
        // TODO: CJW, movie가 자신한테 온 것인지 valid 필요

        String filename = movieMapper.getMoviePath(movie_uid);
        filename="/"+filename;
        // 파일 리소스 리턴
        return fileService.loadAsResource(filename);
    }
}
