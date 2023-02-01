package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.FlaskModelDTO;
import com.famillink.model.mapper.FlaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class FlaskServiceImpl implements FlaskService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;
    private final FileService fileService;

    private final FlaskMapper flaskMapper;


    @Override
    public void sender(FlaskModelDTO sender, MultipartFile file) throws Exception {

    }

    @Override
    public InputStreamResource download(Long movie_uid) throws Exception {
        return null;
    }

    @Override
    public void setRead(Long movie_uid) throws Exception {

    }
}
