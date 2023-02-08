package com.famillink.model.service;


import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.PhotoSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    @Value("${spring.servlet.multipart.location}")
    private String photoPath;


    private final PhotoFileService fileService;
    private final MemberMapper memberMapper;


    @Override
    public void sender(PhotoSenderDTO sender, MultipartFile file) throws Exception {


        //보내는 쪽이 인증과정을


        //우선은 보내는 쪽과 받는 쪽이 정말 같은 가족에 속해있는지를 파악을 한다
        String name = sender.getName();
        Map map = new HashMap<>();
        map.put("user_uid", sender.getFrom_account_uid());//가족의 uid를 넣을거임
        map.put("name", name);//받는 쪽의 name을 설정을 함
        Member m1 = null;


        Optional<Member> temp = memberMapper.findUserByNametoAll(map);//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함

        if (temp.isPresent()) {
            m1 = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }

        //보낸 파일을 저장하고자 함
        fileService.store(file, sender.getFrom_account_uid().toString(), name);


    }

    @Override
    public InputStreamResource download(String name, Authentication authentication) throws Exception {


        Account auth = (Account) authentication.getPrincipal();
        Map map = new HashMap<>();
        map.put("user_uid", auth.getUid());//가족의 uid를 넣을거임
        map.put("name", name);//받는 쪽의 name을 설정을 함
        Optional<Member> temp = memberMapper.findUserByNametoAll(map);
        Member m1 = null;//지금 로그인 한 사람이 해당 해당 가족에 속해 있는지 확인을 하는 과정임
        if (temp.isPresent()) {
            m1 = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        //보낸 파일을 저장하고자 함
        //upfiles/Photo/4/efef.jpg

        Path temppath = Paths.get("upfiles", "Photo", auth.getUid().toString(), name + ".jpg");
        File f = (temppath).toFile();
        // 폴더 생성: mkdir()
        if (!f.exists()) {    // 폴더가 존재하는지 체크
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }

        String templast = temppath.toString();
        templast = "./" + templast;
        // 파일 리소스 리턴
        return fileService.loadAsResource(templast);


    }

    @Override
    public void delete(String name,Authentication authentication) {
        
        //삭제하고자 접근한 가족과 삭제 되는 대상이 같은 가족에 속하는지 판단을 해야함
        Account auth = (Account) authentication.getPrincipal();
        Map map = new HashMap<>();
        map.put("user_uid", auth.getUid());//가족의 uid를 넣을거임
        map.put("name", name);//받는 쪽의 name을 설정을 함
        Optional<Member> temp = memberMapper.findUserByNametoAll(map);
        Member m1 = null;//지금 로그인 한 사람이 해당 해당 가족에 속해 있는지 확인을 하는 과정임
        if (temp.isPresent()) {
            m1 = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }


        Path path=Paths.get("upfiles", "Photo", auth.getUid().toString(), name + ".jpg");
        File file = new File(path.toString());
        if( file.exists() ){
            if(!file.delete()){
                System.out.println("파일삭제 성공");
                throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
            }
        }else{
            throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);

        }
    }
}
