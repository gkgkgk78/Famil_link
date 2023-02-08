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
public class FileServiceImpl implements FileService {
    @Value("upfiles")
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
    public String store(MultipartFile file, String user) {
        try {
            if (file.isEmpty()) {
                throw new Exception("ERROR : File is empty.");
            }
            Path root = Paths.get(moviePath);
            if (!Files.exists(root)) {
                init();
            }

            //파일명 랜덤 저장
            long millis = System.currentTimeMillis();
            UUID uuid = UUID.randomUUID();
            String u1 = uuid.toString() + Long.toString(millis);//밀리초 까지 해서 저장 하고자 함
            try (InputStream inputStream = file.getInputStream()) {

                File f = root.resolve(Paths.get("family", user)).toFile();
                // 폴더 생성: mkdir()
                if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
                    try {
                        f.mkdirs();
                    } catch (Exception e) {
                        throw new BaseException(ErrorMessage.NOT_MAKE_FILE);
                    }
                }


                Path target = (Path) Paths.get("family", user, u1.toString() + file.getOriginalFilename());
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

    @Override
    public void deleteFile(String fileName) throws Exception {

        Path root = Paths.get(moviePath);


        File upload = root.resolve("faceRegist").toFile();

        File target = new File(upload, fileName);

        //삭제할 파일이 없는 경우
        if(!target.exists())
            throw new BaseException(ErrorMessage.NOT_FOUND_FILE);

        //파일 삭제
        boolean result = target.delete();


        File thumbnail = new File(target.getParent(),"s_"+target.getName());
        //getParent() - 현재 File 객체가 나태내는 파일의 디렉토리의 부모 디렉토리의 이름 을 String으로 리턴해준다.
        result = thumbnail.delete();

    }

    @Override
    public String saveRegistVideo(MultipartFile files,String name, Long account_uid) throws IOException {
        if (files.isEmpty()) {
            throw new BaseException(ErrorMessage.NOT_FOUND_FILE);
        }

        //String uploadPath = servletContext.getRealPath("/file");

        //test
        //File upload = new File("C:\\registFace");

        Path root = Paths.get(moviePath);

        if (!Files.exists(root)) {
            init();
        }


        //test
        //File upload = root.resolve(Paths.get("C:\\", "face")).toFile();

        String accountUid = String.valueOf(account_uid);

        File upload = root.resolve("faceRegist").toFile();

        if (!upload.exists()) upload.mkdir();

        String fileName = accountUid + "_" + name + "_" + files.getOriginalFilename();

        File target = new File(upload, fileName);

        InputStream inputStream = files.getInputStream();

        if (!files.isEmpty()) {
	        // 파일 저장
            Path path = (Path) Paths.get("faceRegist", fileName);
            Files.copy(inputStream, root.resolve(path), StandardCopyOption.REPLACE_EXISTING);

        }

        return target.toString();

    }




}
