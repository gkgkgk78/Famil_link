package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlaskFileServiceImpl implements FlaskFileService {
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
    public String storeModel(MultipartFile file, String user) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            Path root = Paths.get(moviePath);
            if (!Files.exists(root)) {
                init();
            }
            try (InputStream inputStream = file.getInputStream()) {
                File f = root.resolve(Paths.get("Flask", user,"Model")).toFile();//여기까지 해서 upfiles/Flask/4/Model로만듦
                // 폴더 생성: mkdir()
                if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
                    try {
                        f.mkdirs();
                    } catch (Exception e) {
                        throw new BaseException(ErrorMessage.NOT_MAKE_FILE);
                    }
                }

                Path target = (Path) Paths.get("Flask", user, "Model" , "model.h5");//이렇게 이름을 저장하고자 함
                Files.copy(inputStream, root.resolve(target), StandardCopyOption.REPLACE_EXISTING);
                return root.resolve(target).toString();
            }

        } catch (Exception e) {
            //파일 저장 불가시 처리하기 위한 부분
            throw new BaseException(ErrorMessage.NOT_STORE_FILE);
        }
    }

    @Override
    public String storeLabel(MultipartFile file, String user) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            Path root = Paths.get(moviePath);
            if (!Files.exists(root)) {
                init();
            }
            try (InputStream inputStream = file.getInputStream()) {
                File f = root.resolve(Paths.get("Flask", user,"Label")).toFile();//여기까지 해서 upfiles/Flask/4/Model로만듦
                // 폴더 생성: mkdir()
                if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
                    try {
                        f.mkdirs();
                    } catch (Exception e) {
                        throw new BaseException(ErrorMessage.NOT_MAKE_FILE);
                    }
                }

                Path target = (Path) Paths.get("Flask", user, "Label" , "labels.txt");//이렇게 이름을 저장하고자 함
                Files.copy(inputStream, root.resolve(target), StandardCopyOption.REPLACE_EXISTING);
                return root.resolve(target).toString();
            }

        } catch (Exception e) {
            //파일 저장 불가시 처리하기 위한 부분
            throw new BaseException(ErrorMessage.NOT_STORE_FILE);
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
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }
    }


}
