package com.famillink.model.service;

import com.famillink.model.domain.param.MovieSenderDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.util.stream.Stream;

public interface MovieService {


    void sender(MovieSenderDTO sender, MultipartFile file) throws Exception;

    InputStreamResource download(Long movie_uid) throws Exception;




}
