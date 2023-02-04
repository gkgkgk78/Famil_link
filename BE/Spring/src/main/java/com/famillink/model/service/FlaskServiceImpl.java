package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import com.famillink.model.mapper.AccountMapper;
import com.famillink.model.mapper.FlaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class FlaskServiceImpl implements FlaskService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;
    private final FlaskFileService fileService;

    private final FlaskMapper flaskMapper;

    private final AccountMapper accountMapper;


    //모델 보내고자 할시에 작동을 함
    @Override
    public void send_model(Account sender, MultipartFile file) throws Exception {

        Account a1 = accountMapper.findUserByEmail(sender.getEmail()).get();
        String get = fileService.storeModel(file, a1.getUid().toString());//이렇게 해서 저장을 함


    }

    //label을 보내고자 할시에 작동을 함
    @Override
    public void send_label(Account sender, MultipartFile file) throws Exception {
        Account a1 = accountMapper.findUserByEmail(sender.getEmail()).get();
        String get = fileService.storeLabel(file, a1.getUid().toString());//이렇게 해서 저장을 함

    }

    //얼굴 인증을 하고자 할시에 임시로 저장을 하기 위해 진행하는 메서드이다.
    @Override
    public String send_temp(Account sender, MultipartFile file) throws Exception {

        Account a1 = accountMapper.findUserByEmail(sender.getEmail()).get();
        String get = fileService.storeTemp(file, a1.getUid().toString());//이렇게 해서 저장을 함
        return "./" + get;

    }

    @Override
    public void delete_temp(String path) {
        fileService.deleteTemp(path);
    }

    @Override
    public InputStreamResource read_model(String email) throws Exception {

        //이제 모델을 불러오면 됨

        //upfiles/Flask/가족uid/Model
        Account temp = accountMapper.findUserByEmail(email).get();

        Path temppath = Paths.get("upfiles", "Flask", temp.getUid().toString(), "Model", "model.h5");
        File f = (temppath).toFile();
        // 폴더 생성: mkdir()
        if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }

        String templast = temppath.toString();
        templast = "./" + templast;
        // 파일 리소스 리턴
        return fileService.loadAsResource(templast);
    }

    @Override
    public InputStreamResource read_label(String email) throws Exception {
        Account temp = accountMapper.findUserByEmail(email).get();

        Path temppath = Paths.get("upfiles", "Flask", temp.getUid().toString(), "Label", "labels.txt");
        File f = (temppath).toFile();
        // 폴더 생성: mkdir()
        if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }

        String templast = temppath.toString();
        templast = "./" + templast;
        // 파일 리소스 리턴
        return fileService.loadAsResource(templast);


    }


}
