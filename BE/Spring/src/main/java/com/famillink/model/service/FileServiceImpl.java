package com.famillink.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(moviePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            Path root = Paths.get(moviePath);
            if (!Files.exists(root)) {
                init();
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, root.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }


    @Override
    public Path load(String filename) {
        // TODO Auto-generated method stub
        return Paths.get(moviePath).resolve(filename);
    }

    @Override
    public InputStreamResource loadAsResource(String filename) throws Exception {
        try {
            return new InputStreamResource(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

}
