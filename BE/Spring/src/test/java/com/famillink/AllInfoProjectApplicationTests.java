package com.famillink;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.famillink.controller.*;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.ImageDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.util.JwtTokenProvider;
import lombok.Data;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author cjw.git
 *
 * 1. 회원가입
 * 2. 회원로그인
 * 3. 멤버추가
 * 4. 멤버로그인
 *
 *
 * 주의사항
 * 1. class 위 @Transactional 가 존재하면 실제 DB에 반영되지 않는다.
 * 2. 로그인 할 때 member_uid가 오기 때문에 멤버를 추가하고 다시 로그인하여 멤버 uid를 받아야한다.
 * 3. @Disabled 로 Test 비활성화 가능하다.
 * 현재 결론
 * 1. 회원가입 돌리고 -> 로그인되고 -> 에러,
 *    회원가입 비활성화하고 로그인 -> 멤버추가 -> 에러,
 *    회원가입 비활성화하고 로그인 -> 멤버로그인 -> 성공
 *    
 * 과 같은 절차를 따른다.
 * 추후 fix예정
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

    private String member_access_token;
    private String member_refresh_token;

}

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllInfoProjectApplicationTests {
    private final Logger logger = LoggerFactory.getLogger(AllInfoProjectApplicationTests.class);

    @Autowired
    private AccountController accountController;
    @Autowired
    private MemberController memberController;
    @Autowired
    private MovieController movieController;
    @Autowired
    private ScheduleController scheduleController;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @Order(1)
    void contextLoads() {
        assert accountController != null;
        assert memberController != null;
        assert movieController != null;
        assert scheduleController != null;
    }

    @Test
    @Order(2)
    @Disabled
    void 회원가입_테스트() throws Exception {
        Account account = new Account();
        account.setEmail("test@test.com");
        account.setPw("ss1235789");
        account.setAddress("경기도 테스트");
        account.setNickname("테스트 가족");
        account.setPhone("0101234789");
        try {
            ResponseEntity<?> temp = accountController.signup(account);
            assert temp.getStatusCode().value() == 200;
            Map<String, Object> result = (Map<String, Object>) temp.getBody();
            assert (boolean) result.get("result");
            logger.debug(result.get("msg").toString());
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(3)
    void 회원로그인_테스트() throws Exception {
        Account account = new Account();
        account.setEmail("test@test.com");
        account.setPw("ss1235789");
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
            if (result.containsKey("members")) {
                long member_uid = ((List<Member>) result.get("members")).get(0).getUid();
                assert member_uid > 0;
                TestData.getInstance().setMember_user_uid(member_uid);
            }
            logger.debug(result.get("msg").toString());
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(4)
    @Disabled
    void 멤버_추가() throws Exception {
        Member member = new Member();
        try {
            String token = TestData.getInstance().getAccount_access_token();
            assert token != null;

            Authentication authentication = doFilter(token, "qwer/member/signup");
            assert authentication != null;

            ResponseEntity<?> temp = memberController.signup("테스트", "TEST", authentication);
            assert temp.getStatusCode().value() == 200;
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    @Order(5)
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
                    new FileReader("C:\\Users\\SSAFY\\Documents\\PROJECT\\S08P12A208\\BE\\Spring\\src\\test\\java\\com\\famillink\\face.txt")
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
            logger.info(e.getErrorMessage().toString());
        }
        return null;
    }
}
