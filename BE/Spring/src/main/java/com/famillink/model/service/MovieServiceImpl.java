package com.famillink.model.service;

import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.MovieDTO;
import com.famillink.model.domain.param.MovieOccur;
import com.famillink.model.domain.param.MovieSenderDTO;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.MemberMapper;
import com.famillink.model.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.nio.file.Files;
import java.util.*;


@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    @Value("${spring.servlet.multipart.location}")
    private String moviePath;
    private final FileService fileService;
    private final MovieMapper movieMapper;
    private final MemberMapper memberMapper;
    private final MemberService mservice;

    private final SseService sseService;


    @Override
    @Transactional
    public void sender(MovieSenderDTO sender, MultipartFile file) throws Exception {

        //현재는 sender에 있는 보내고자 하는 얘들이 같은
        Member m;
        try {
            if (!mservice.findTogether(sender))//계정 정보가 일치 하지 않을시에 처리 하고자 하는 상황
            {
                throw new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO);
            }
        } catch (Exception e) {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);
        }

        m = memberMapper.findUserByUid(sender.getFrom_member_uid()).get();
        //가족 uid로 폴더에 저장을 해줌
        String get = fileService.store(file, m.getUser_uid().toString());

        //TODO: 이벤트 리스너
        notifyStoreInfo(sender.getTo_member_uid());

        movieMapper.sendMovie(sender, get);

    }

    private void notifyStoreInfo(Long member_to) {

        sseService.send(member_to, "new video", "http://i8a208.p.ssafy.io:3000/movie");

    }

    @Override
    public StreamingResponseBody download(Long movie_uid, HttpHeaders httpHeaders, Authentication authentication) throws Exception {

        try {
            //movie에서 받은 사람이 자기 자신에게 온 영상인지를 파악을 해야함
            //가져온 무비를 기반으로 
            MovieSenderDTO movie = movieMapper.getMovie(movie_uid);
            Long get = movie.getTo_member_uid();//받은 사람의 uid와 로그인 해서 받은 사람의 uid를 비교를 해야함

            Member auth = (Member) authentication.getPrincipal();

            if (!get.equals(auth.getUid()))
                throw new BaseException(ErrorMessage.NOT_GET_FILE);
            //여기까지 해서 받은 사용자에게 온 영상인지를 파악을 했음

            MovieSenderDTO sender = movieMapper.getMovie(movie_uid);
            if (!mservice.findTogether(sender))//계정 정보가 일치 하지 않을시에 처리 하고자 하는 상황
            {
                throw new BaseException(ErrorMessage.NOT_MATCH_ACCOUNT_INFO);
            }


        } catch (Exception e) {
            //System.out.println(e);
            //throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
        }
        String filename = movieMapper.getMoviePath(movie_uid);
        filename = "./" + filename;
        File file = new File(filename);
        if (!file.isFile()) {
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        }
        httpHeaders.add("Content-Length", Long.toString(file.length()));

        // 파일 리소스 리턴
        return outputStream -> {
            FileCopyUtils.copy(Files.newInputStream(file.toPath()), outputStream);
        };
    }


    @Override
    public void setRead(Long movie_uid) throws Exception {

        int now = movieMapper.getOneMovie(movie_uid);
        if (now != 1) {
            throw new BaseException(ErrorMessage.NOT_READ_FILE);
        } else {
            movieMapper.setMovie(movie_uid);
        }

    }

    @Override
    public List<MovieDTO> showMovieList(Long member_to) throws Exception {

        List<MovieDTO> list = movieMapper.findMovieByMemberTo(member_to);
        return list;
    }

    @Override
    public Map<String, Object> getAccountList(Member member) throws Exception {

        //속한 모든 가족을 찾음
        List<Long> allaccount = memberMapper.getAccount(member.getUser_uid());
        int checkz = 0;
        //이제가족에 해당이 되는 모든것을 찾았으니 한번 연산을 해보도록 하자

        Optional<MovieOccur> object = movieMapper.findMinDate(member.getUid());
        Long result = null;
        if (!object.isPresent()) {
            throw new BaseException(ErrorMessage.NOT_EXIST_RECORD);
        } else {
            result = object.get().getUid();
            //보내지 않은 가족이 존재하다면 보내줘야함
            //돌면서 안보낸 가족이 있는지 확인을 해야함
            for (Long l1 : allaccount) {
                if (!l1.equals(member.getUid())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("member_from", member.getUid());
                    map.put("member_to", l1);
                    int check = movieMapper.findMovieCount(map);
                    if (check == 0) {
                        result = l1;
                        checkz = 1;
                        break;
                    }
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        Member mm = memberMapper.findUserByUid(result).get();

        response.put("name", mm.getName());
        if (checkz == 1) {
            response.put("date", "-1");
        } else {
            response.put("date", object.get().getSdate());
        }

        return response;
    }


}
