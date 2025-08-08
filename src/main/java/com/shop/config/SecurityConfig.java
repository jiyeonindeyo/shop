package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// └>해당 어노테이션이 달린 클래스에 @Bean 어노테이션이 붙은 메서드를 등록하면 해당 메서드의 반환 값이 스프링 빈으로 등록됨
@EnableWebSecurity
public class SecurityConfig {

    // spring security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //form 데이터. id, pw 방식을 통한 로그인
        http.formLogin((it) -> it
                            .loginPage("/members/login")
                            .defaultSuccessUrl("/")
                            .usernameParameter("email") //login폼에서 name속성에 email을 넣어야함
                            .failureUrl("/members/login/error")
        );

        //csrf 검사 하지않도록 설정(logout이 get이라 get일경우 csrf가 있어야돼서 꺼버리기
//        http.csrf((csrf) -> csrf.disable()); // = http.csrf(AbstractHttpConfigurer::disable) 같다~

        http.logout((it) -> it
                            .logoutUrl("/members/logout")
                            .logoutSuccessUrl("/")
        );
        return http.build();
    }

    // 데이터 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
