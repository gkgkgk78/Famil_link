package com.famillink.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.famillink.exception.BaseException;
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

//
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
        DecodedJWT decodedJWT = JWT.decode(token);
        String level_type=decodedJWT.getClaims().get("level").toString();
        System.out.println(level_type);
        boolean flag=false;

        String [] route=requestURI.split("/");//rest api 경로를 의미를 함 1:member/account , 2:구체적인 경로
        System.out.println(route[1]);
        //member인지, account인지에 따라 다른 메서드를 호출을 해야함
        try {

            if (level_type.equals("account"))

            {
                flag=jwtTokenProvider.validateToken(token);
            }
            else if (level_type.equals("member"))
            {
                flag=jwtTokenProvider.validateToken1(token);
            }

            if (StringUtils.hasText(token) && flag) {//토큰에 대한 유효성을 검사를 함

                //접근 가능한 경우
                //가족계정을 가지고 member에 회원가입 하고자 할시(그전 이 가족 일시만 가능함)
                if (route[1].equals("member")) {

                    //member의 컨트롤러로 가고자 할시에 
                    if(level_type.equals("account"))//account가 member의 컨트롤러로 갈수 있는 경우는
                        //회원가입이나, 로그인을 할시에만 가능하다
                    {
                        if(!route[2].equals("login") || !route[2].equals("signup"))
                            return;
                    }
                }
                //접근 불가능한 경우
                //가족 계정을 가지고 member의 refresh




                Authentication authentication = jwtTokenProvider.getAuthentication(token);//토큰이 유효할시 Authentication객체를 생성해서 SecurityContextHolder에 추가함
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Security context에 인증 정보를 저장했습니다, uri: {}", requestURI);
            }





            else {
                logger.debug("유효한 Jwt 토큰이 없습니다, uri: {}", requestURI);
            }
        } catch (BaseException e) {
            logger.info(e.getErrorMessage().toString());
        }

        chain.doFilter(request, response);
    }
}
