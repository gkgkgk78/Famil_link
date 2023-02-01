package com.famillink.config.security;

import com.famillink.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor

//SecurityConfig : WebSecurityConfigurerAdapter를 extends받아 Spring Security 설정 역할을 담당하는 핵심 클래스다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    //httpSecurity파라미터를 받은 configure메서드
    //스프링 Security의 설정은 대부분 HttpSecurity를 통해 진행이 된다.
    //HttpSecurity 의 주요기능
    //1.리소스 접근 권한 설정 2. 인증 실패시 발생하는 예외 처리 3. 인증 로직 커스터마이징 4. csrf,cors등의 스프링 시큐리티 설정
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // 스프링 시큐리티 에서 가진 ui 사용 안하게 설정
                .csrf().disable() // csrf 사용 안함,csrf는 요청자가 의도치 않게 서버에 공격하는것을 방지하는 token 검증 옵션
                //post,put,delete 요청일경우에 csrf 검증을 한다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함

                .and()
                .authorizeRequests()//경로에 권한, 인증 설정을 한다는 것임
                .antMatchers(//밑에 게시된 경로들에 대한 요청을 승인할수 있다
                        "/account/signup",
                        "/account/login",
                        "/account/refresh",
                        "/account/mail",
                        "/account/check/**",
                        "/account/find/**",
                        "/member/signup/**",
                        "/member/login/**",
                        "/member/refresh",
                        "/member/auth",
                        "/member/pick",
                        "/movie/**",
                        "/streaming/**"
                )
                .permitAll()//모든 인증을 요구를 하지는 않지만
                .anyRequest().hasRole("USER")//USER라는 권한을 가진 회원은 , 위에 설정된 경로에 대해서 권한, 인증설정을 한다
                //hasRole("USER")라고 저장을 하였지만 “ROLE_USER”로 UsernamePasswordAuthenticationToken객체에 값을 담아야 한다.

                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())//권한을 확인하는 과정에서 통과하지 못하는 예외가 발생할 경우 예외를 전달을 한다
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())//인증과정에서 예외가 발생할 경우 예외를 전달한다.

                .and()
                .cors()
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // UsernamePasswordAuthenticationFilter 앞에 JwtFilter를 추가하겠다
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Override
    //WebSecurity 파라미터를 받은 configure 메서드
    //httpSecurity앞단에 적용이 되며 , 스프링 시큐리티의 영향권 밖에 존재를 한다.,
    //즉 인증과 인가가 적용되기 전에 동작되는 설정임., 인증과 인가가 적용되지 않는 리소스 접근에 대해서만 사용한다.
    //밑의 코드는 swagger에 적용되는 경로들은 인증과 인가를 무시하게 설정을 한것.
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/swagger-ui/**"); // swagger 관련 요청은 허용
    }
}
