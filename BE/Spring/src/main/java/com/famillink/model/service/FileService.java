package com.famillink.model.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {
    void init();

    void store(MultipartFile file);

    Path load(String filename);

    Resource loadAsResource(String filename);
}
