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
public class PhotoFileServiceImpl implements PhotoFileService {
    @Value("upfiles")
    private String photoPath;

    @Override
    public void init() {
        try {
            Files.createDirectories(Paths.get(photoPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    @Override
    public String store(MultipartFile file, String user,String name) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            Path root = Paths.get(photoPath);
            if (!Files.exists(root)) {
                init();
            }
            try (InputStream inputStream = file.getInputStream()) {
                File f = root.resolve(Paths.get("Photo", user)).toFile();//여기까지 해서 upfiles/Photo/4/로만듦
                // 폴더 생성: mkdir()
                if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
                    try {
                        f.mkdirs();
                    } catch (Exception e) {
                        throw new BaseException(ErrorMessage.NOT_MAKE_FILE);
                    }
                }

                Path target = (Path) Paths.get("Photo", user, name+".jpg");//이렇게 이름을 저장하고자 함
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
        return Paths.get(photoPath).resolve(filename);
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
