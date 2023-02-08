package com.famillink;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.famillink.controller.*;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.ImageDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.mapper.AccountMapper;
import com.famillink.util.JwtTokenProvider;
import lombok.Data;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author cjw.git
 * @author SSAFY
 * @version 1.1
 * @apiNote DB를 초기화 후 해야합니다. 회원가입 후 log창에 logger.info로 표시되면 DB 가서 level을 1로 바꾸어줘야합니다.
 * <p>
 * BE 자동으로 코드를 테스트해준다.
 * 1. 가족계정 가입
 * 2. 가족계정 로그인
 * 3. 모델 저장 (flask를 위해)
 * 4. 멤버 조회 (처음엔 아무도 없음)
 * 5. 멤버 추가
 * 6. 멤버 조회1 (한명이 나옴)
 * 7. 멤버로그인
 */
@Data
class TestData {
    private static TestData instance;

    public static TestData getInstance() {
        if (instance == null)
            instance = new TestData();
        return instance;
    }

    private String account_access_token;
    private String account_refresh_token;
    private Long account_user_uid;
    private Long member_user_uid;
    private String member_name;
    private String member_access_token;
    private String member_refresh_token;

}

@SpringBootTest
//@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllInfoProjectApplicationTests {


    @Autowired
    private AccountController accountController;
    @Autowired
    private MemberController memberController;
    @Autowired
    private MovieController movieController;
    @Autowired
    private FlaskController flaskController;
    @Autowired
    private ScheduleController scheduleController;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AccountMapper accountMapper;

    @Test
    @Order(10)
    void contextLoads() {
        assert accountController != null;
        assert memberController != null;
        assert movieController != null;
        assert scheduleController != null;
    }

    @Test
    @Order(20)
//    @Disabled
    void 회원가입_테스트() throws Exception {
        Account account = new Account();
        account.setEmail(email);
        account.setPw(password);
        account.setAddress(account_address);
        account.setNickname(account_nickname);
        account.setPhone(phone);
        try {
            ResponseEntity<?> temp = accountController.signup(account);
            assert temp.getStatusCode().value() == 200;
            Map<String, Object> result = (Map<String, Object>) temp.getBody();
            assert (boolean) result.get("result");
            logger.debug(result.get("msg").toString());

            logger.info("10초 이내 회원인증을 완료해주세요.");
            Thread.sleep(10000);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(30)
    void 회원로그인_테스트() throws Exception {
        Account account = new Account();
        account.setEmail(email);
        account.setPw(password);
        try {
            ResponseEntity<?> temp = accountController.loginUser(account);
            assert temp.getStatusCode().value() == 200;

            Map<String, Object> result = (Map<String, Object>) temp.getBody();
            assert (boolean) result.get("result");
            assert result.get("access-token") != null;
            assert result.get("refresh-token") != null;
            assert result.get("uid") != null;
            assert Long.parseLong(result.get("uid").toString()) > 0;

            TestData.getInstance().setAccount_user_uid(Long.parseLong(result.get("uid").toString()));
            TestData.getInstance().setAccount_access_token(result.get("access-token").toString());
            TestData.getInstance().setAccount_access_token(result.get("refresh-token").toString());

            logger.debug(result.get("msg").toString());
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(33)
    void 모델저장() throws Exception {
        String token = TestData.getInstance().getAccount_access_token();
        assert token != null;

        Authentication authentication = doFilter(token, "qwer/member/signup");
        assert authentication != null;

        Account auth = (Account) authentication.getPrincipal();

        Optional<Account> temp = accountMapper.findUserByUid(auth.getUid());//이렇게 해서 가족중에서 보낸 name를 가진자가 있는지 판단을함
        Account account=null;
        if (temp.isPresent()) {
            account = temp.get();
        } else {
            throw new BaseException(ErrorMessage.NOT_USER_INFO);//보낸 가족 정보와 일치하는 유저 정보가 없음을 의미를 함
        }

        MultipartFile model = new MockMultipartFile("keras_model.h5", Files.newInputStream(new File("C:\\Users\\SSAFY\\Documents\\PROJECT\\BE\\Flask\\temp\\model.h5").toPath()));
        MultipartFile label = new MockMultipartFile("labels.txt", Files.newInputStream(new File("C:\\Users\\SSAFY\\Documents\\PROJECT\\BE\\Flask\\temp\\labels.txt").toPath()));

        flaskController.addModel(account.getUid(), model);
        flaskController.addLabel(account.getUid(), label);


        flaskController.addLabel(account.getUid());
        flaskController.addModel(account.getUid());



    }

    @Test
    @Order(35)
    void 멤버_조회() throws Exception {
        String token = TestData.getInstance().getAccount_access_token();
        assert token != null;

        Authentication authentication = doFilter(token, "qwer/member/signup");
        assert authentication != null;

        try {
            ResponseEntity<?> temp = accountController.getMembers(authentication);

        } catch (BaseException be) {
            assert be.getHttpStatus().value() == 400;
        }
    }

    @Test
    @Order(40)
//    @Disabled
    void 멤버_추가() throws Exception {
        try {
            String token = TestData.getInstance().getAccount_access_token();
            assert token != null;

            Authentication authentication = doFilter(token, "qwer/member/signup");
            assert authentication != null;

            ResponseEntity<?> temp = memberController.signup(member_name, member_nickname, authentication);
            assert temp.getStatusCode().value() == 200;
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(45)
    void 멤버_조회1() throws Exception {
        String token = TestData.getInstance().getAccount_access_token();
        assert token != null;

        Authentication authentication = doFilter(token, "qwer/member/signup");
        assert authentication != null;

        try {
            ResponseEntity<?> temp = accountController.getMembers(authentication);
            assert temp.getStatusCode().value() == 200;

            List<Member> response = (ArrayList<Member>) temp.getBody();
            assert response.size() > 0;

            assert response.get(0).getUid() != null;
            assert response.get(0).getName() != null;

            assert response.get(0).getUid() > 0;
            assert !response.get(0).getName().equals("");

            TestData.getInstance().setMember_user_uid(response.get(0).getUid());
            TestData.getInstance().setMember_name(response.get(0).getName());

        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(50)
    void 멤버로그인_테스트() throws Exception {
        ImageDTO imageDTO = new ImageDTO();
        try {
            String token = TestData.getInstance().getAccount_access_token();
            assert token != null;

            Authentication authentication = doFilter(token, "qwer/member/login");
            assert authentication != null;

            imageDTO.setUid(TestData.getInstance().getMember_user_uid());
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new FileReader("C:\\Users\\SSAFY\\Documents\\PROJECT\\BE\\Spring\\src\\test\\java\\com\\famillink\\face.txt")
            );
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            str = sb.toString().replace("[", "")
                    .replace("]", "")
                    .replace(",", " ").replace("  ", " ");
            Scanner sc = new Scanner(str);
            List<List<List<Integer>>> temp = new ArrayList<>();
            for (int y = 0; y < 224; y++) {
                List<List<Integer>> temp1 = new ArrayList<>();
                for (int x = 0; x < 224; x++) {
                    List<Integer> temp2 = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        temp2.add(sc.nextInt());
                    }
                    temp1.add(temp2);
                }
                temp.add(temp1);
            }
            imageDTO.setJson(temp);
            ResponseEntity<?> response = memberController.login(imageDTO, authentication);
            assert response.getStatusCode().value() == 200;

            Map<String, Object> result = (Map<String, Object>) response.getBody();
            assert (boolean) result.get("result");
            assert result.get("access-token") != null;
            assert result.get("refresh-token") != null;
            assert result.get("uid") != null;
            assert Long.parseLong(result.get("uid").toString()) > 0;

//            TestData.getInstance().setMember_user_uid(Long.parseLong(result.get("uid").toString()));
            TestData.getInstance().setMember_access_token(result.get("access-token").toString());
            TestData.getInstance().setMember_access_token(result.get("refresh-token").toString());

            logger.debug(result.get("msg").toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public Authentication doFilter(String _token, String _route) throws IOException, ServletException {
        String token = _token;
        //token복호화 하기 위해서 진행 하는 과정임
        DecodedJWT decodedJWT = null;
        String level_type = null;
        String[] route = _route.split("/");//보낸 restapi경로를 파악을 하여 처리를 하고자 하는 부분임 ,1:member/account, 2: login/signup등등
        boolean flag = false;

        if (token != null)//여기에 해당이 되지 않는 다면 아직 토큰을 가지고 있지 않은 상태를 의미를 한다.
        {
            decodedJWT = JWT.decode(token);
            level_type = decodedJWT.getClaims().get("level").toString();//level은 member,account 둘중 하나를 내포 하고 있다.
            level_type = level_type.substring(1, level_type.length() - 1);
        }
        //member인지, account인지에 따라 다른 메서드를 호출을 해야함
        try {
            if (token == null) {
                //권한이 없는 경우에는 접근하고자 하는 경로가 signup과 login일때만 가능하게 해야겠다
                //이렇게 처리를 할시 초기에 접근하는 주소가 존재를 할시에 처리를 해주는 방법을 고려 해 봐야 한다.
//                if(route ==null)
//                    throw  new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
                if (route != null) {
                    if (route.length <= 1)
                        throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
                    if (route[1].equals("member"))
                        throw new BaseException(ErrorMessage.NOT_PERMISSION_EXCEPTION);//token정보가 없을시에는 member에 접근하는건 불가능하게 함
                }

            } else {//토큰이 존재하는 경우를 의미를 함
                if (level_type.equals("account")) {
                    flag = jwtTokenProvider.validateToken(token);
                    if (flag == false)
                        return null;
                    //접근 가능한 경우
                    //가족계정을 가지고 member에 회원가입 하고자 할시(그전 이 가족 일시만 가능함)
                    if (route[1].equals("member")) {
                        //member의 컨트롤러로 가고자 할시에
                        if (level_type.equals("account"))//account가 member의 컨트롤러로 갈수 있는 경우는
                        //회원가입이나, 로그인을 할시에만 가능하다
                        {
                            boolean next = false;
                            if ((route[2].equals("login")))
                                next = true;
                            if ((route[2].equals("signup")))
                                next = true;
                            if ((route[2].equals("auth")))
                                next = true;

                            if (next == false)
                                throw new BaseException(ErrorMessage.NOT_PERMISSION_EXCEPTION);//account의 계정으로는 member의 login,signup을 제외하고 접근 불가
                        }
                    }

                } else if (level_type.equals("member")) {
                    flag = jwtTokenProvider.validateToken1(token);
                    if (flag == false)
                        return null;
                }
                Authentication authentication = null;

                //account에서 member로 로그인을 할시에 만 token의 처리를 해줘야 함
                if (level_type.equals("account")) {
                    //이때는 authorization에 account정보가 들어가게됨
                    authentication = jwtTokenProvider.getAuthentication(token);//토큰이 유효할시 Authentication객체를 생성해서 SecurityContextHolder에 추가함


                } else {
                    //이때는 authorization에 member의 정보가 들어가야함
                    authentication = jwtTokenProvider.getAuthentication1(token);
                }

                return authentication;
            }
        } catch (BaseException e) {

        }
        return null;
    }

    private final Logger logger = LoggerFactory.getLogger(AllInfoProjectApplicationTests.class);
    private static final String email = "cjw.git@gmail.com";
    private static final String password = "ss1235789";
    private static final String phone = "01046182150";
    private static final String account_nickname = "진우네 가족";
    private static final String account_address = "경기도 고양시 덕양구 성사동";
    private static final String member_name = "최진우";
    private static final String member_nickname = "CJW";
}
