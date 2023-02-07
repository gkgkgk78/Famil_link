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
import java.util.Optional;


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
    public void send_model(Long uid, MultipartFile file) throws Exception {


        Optional<Account> temp = accountMapper.findUserByUid(uid);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account = null;
        if (temp.isPresent()) {
            account = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }

//        Account a1 = accountMapper.findUserByEmail(sender.getEmail()).get();
        String get = fileService.storeModel(file, uid.toString());//이렇게 해서 저장을 함


    }

    //label을 보내고자 할시에 작동을 함
    @Override
    public void send_label(Long uid, MultipartFile file) throws Exception {

        Optional<Account> temp = accountMapper.findUserByUid(uid);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account = null;
        if (temp.isPresent()) {
            account = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        String get = fileService.storeLabel(file, account.getUid().toString());//이렇게 해서 저장을 함

    }

    @Override
    public InputStreamResource read_model(Long uid) throws Exception {

        //이제 모델을 불러오면 됨
        Optional<Account> temp1 = accountMapper.findUserByUid(uid);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account = null;
        if (temp1.isPresent()) {
            account = temp1.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        //upfiles/Flask/가족uid/Model
        Account temp = account;

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
    public InputStreamResource read_label(Long uid) throws Exception {
        //Account temp = accountMapper.findUserByEmail(email).get();

        Optional<Account> temp = accountMapper.findUserByUid(uid);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account = null;
        if (temp.isPresent()) {
            account = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        Path temppath = Paths.get("upfiles", "Flask", account.getUid().toString(), "Label", "labels.txt");
        File f = (temppath).toFile();
        // 폴더 생성: mkdir()
        if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }

        String templast = temppath.toString();
        templast = "./" + templast;

        System.out.println(templast);

        // 파일 리소스 리턴
        return fileService.loadAsResource(templast);


    }

    public String read_label_totext(String email) throws Exception {
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
        return templast;
    }

    public String read_model_totext(String email) throws Exception {

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
        return templast;
    }

}
