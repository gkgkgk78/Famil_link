package com.famillink.model.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    void init();

    String store(MultipartFile file, String user);

    String saveRegistVideo(MultipartFile files, String name, Long account_uid) throws IOException;

    Path load(String filename);

    InputStreamResource loadAsResource(String filename) throws Exception;

    void deleteFile(String fileName) throws Exception;


}
