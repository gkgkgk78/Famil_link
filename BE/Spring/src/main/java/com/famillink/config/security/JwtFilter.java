package com.famillink.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    //GenericFilterBean: 기존 필터에서 가져올수 없는 스프링의 설정 정보를 가져올수 있게 확장된 추상 클래스이다.

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);//jwt토큰을 통해 토큰을 추출함
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        //token복호화 하기 위해서 진행 하는 과정임
        DecodedJWT decodedJWT = null;
        String level_type = null;
        String[] route = null;//보낸 restapi경로를 파악을 하여 처리를 하고자 하는 부분임 ,1:member/account, 2: login/signup등등
        boolean flag = false;

        if (token != null)//여기에 해당이 되지 않는 다면 아직 토큰을 가지고 있지 않은 상태를 의미를 한다.
        {
            decodedJWT = JWT.decode(token);
            level_type = decodedJWT.getClaims().get("level").toString();//level은 member,account 둘중 하나를 내포 하고 있다.
            route = requestURI.split("/");//rest api 경로를 의미를 함 1:member/account , 2:구체적인 경로
            level_type=level_type.substring(1,level_type.length()-1);
        }
        //member인지, account인지에 따라 다른 메서드를 호출을 해야함
        try {

            if (token == null) {
                logger.debug("유효한 Jwt 토큰이 없습니다, uri: {}", requestURI);
            } else {//토큰이 존재하는 경우를 의미를 함
                if (level_type.equals("account")) {
                    flag = jwtTokenProvider.validateToken(token);
                    if (flag == false)
                        return;
                    //접근 가능한 경우
                    //가족계정을 가지고 member에 회원가입 하고자 할시(그전 이 가족 일시만 가능함)
                    if (route[1].equals("member")) {
                        //member의 컨트롤러로 가고자 할시에
                        if (level_type.equals("account"))//account가 member의 컨트롤러로 갈수 있는 경우는
                        //회원가입이나, 로그인을 할시에만 가능하다
                        {
                            if (!route[2].equals("login") || !route[2].equals("signup"))
                                throw new BaseException(ErrorMessage.NOT_PERMISSION_EXCEPTION);//account의 계정으로는 member의 login,signup을 제외하고 접근 불가
                        }
                    }

                } else if (level_type.equals("member")) {
                    flag = jwtTokenProvider.validateToken1(token);
                    if (flag == false)
                        return;
                }

                Authentication authentication = jwtTokenProvider.getAuthentication(token);//토큰이 유효할시 Authentication객체를 생성해서 SecurityContextHolder에 추가함
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Security context에 인증 정보를 저장했습니다, uri: {}", requestURI);

            }

        } catch (BaseException e) {
            logger.info(e.getErrorMessage().toString());
        }

        chain.doFilter(request, response);


    }
}
