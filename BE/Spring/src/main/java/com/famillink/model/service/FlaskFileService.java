package com.famillink.model.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FlaskFileService {
    void init();

    String storeModel(MultipartFile file, String user);
    String storeLabel(MultipartFile file, String user);

    String storeTemp(MultipartFile file, String user);

    void deleteTemp(String path);

    Path load(String filename);

    InputStreamResource loadAsResource(String filename) throws Exception;




}
