package com.famillink.model.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface PhotoFileService {
    void init();

    String store(MultipartFile file, String user,String name);

    Path load(String filename);

    InputStreamResource loadAsResource(String filename) throws Exception;


}
