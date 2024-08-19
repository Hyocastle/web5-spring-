package net.datasa.web5.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //미리 실행하는 내용이 있는 설정을 java 로 짤 때 사용하는 어노테이션
//config라는 것을 알려주는 것
@EnableWebSecurity //security를 쓰겠다는 의미
public class WebSecurityConfig { 
    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/"                 //메인화면  //로그인 없이 접근할 수 있는 페이지
            ,"/images/**"
            , "/css/**"
            , "/js/**"
            , "/thymeleaf"
            , "/member/joinForm"
            , "/member/idCheck"
            , "/member/join"
            , "/board/list"
            , "/board/view/*"
    };

    @Bean //메모리에 미리 준비해 둔 객체 (Controller, service 등도 전부 bean)
    //메서드에 bean이 있는 경우 리턴값을 생성해둔다.
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            //요청에 대한 권한 설정
            .authorizeHttpRequests(author -> author
                .requestMatchers(PUBLIC_URLS).permitAll()   //모두 접근 허용(인증이 필요 없다)
                //public_urls 미리 설정한 링크들
                .anyRequest().authenticated()               //그 외의 모든 요청은 인증 필요
            )
            //HTTP Basic 인증을 사용하도록 설정
            .httpBasic(Customizer.withDefaults())
            //폼 로그인 설정
            .formLogin(formLogin -> formLogin
                    .loginPage("/member/loginForm") //직접 form을 html로 만들어야한다. //로그인폼 페이지 경로
                    .usernameParameter("id")                //폼의 ID 파라미터 이름
                    .passwordParameter("password")          //폼의 비밀번호 파라미터 이름
                    .loginProcessingUrl("/member/login") //submit을 눌렀을 때 처리될 경로 -> 이름만 정하고 만들 필요가 없다
                    //로그인폼 제출하여 처리할 경로 //security가 처리해준다.
                    .defaultSuccessUrl("/")                 //로그인 성공 시 이동할 경로
                    .permitAll()                            //로그인 페이지는 모두 접근 허용
            )
            //로그아웃 설정
            .logout(logout -> logout
                    .logoutUrl("/member/logout")                   //로그아웃 처리 경로 // 경로만 정하면 된다.-> security가 처리해준다
                    .logoutSuccessUrl("/")                  //로그아웃 성공 시 이동할 경로
            );

        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    //비밀번호 암호화를 위한 인코더를 빈으로 등록
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
