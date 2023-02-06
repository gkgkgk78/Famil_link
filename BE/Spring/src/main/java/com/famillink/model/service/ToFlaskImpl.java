package com.famillink.model.service;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ToFlaskImpl implements ToFlask {

    @Override
    public void send(Authentication authentication, String path) throws IOException {
        //쭉 변환 하는 과정입니다
        String filename = "";
        String url = "http://localhost:5000/file";
        String filepath = "";
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        Path temppath = null;
        if (path.equals("label"))
            temppath = Paths.get("./", "upfiles", "Flask", tt.toString(), "Label", "labels.txt");
        else if (path.equals("model"))
            temppath = Paths.get("./", "upfiles", "Flask", tt.toString(), "Model", "model.h5");//이렇게 이름을 저장하고자 함
        File f = (temppath).toFile();
        // 폴더 생성: mkdir()
        if (!f.exists()) {    // 폴더가 존재하는지 체크, 없다면 생성
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }




    }
}
