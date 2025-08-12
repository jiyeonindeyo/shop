package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.properties에 쓰인 파라미터 값을 읽어오는 방법
    @Value("${uploadPath}")
    String uploadPath;

    // 서버컴퓨터의 파일 시스템에 위치한 자원(img 등)을 요청 URL과 매핑하여 응답하도록 함
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //1. Http GET요청(/images/**)이 들어오면
        registry.addResourceHandler("/images/**")
                //2. uploadPath(file:///C:/shop/)에서 동일한 리소스를 찾아서 반환(응답)한다.
                .addResourceLocations(uploadPath);
        //EX1) 1. http GET /images/abc.jpg ━▶ 2. C:/shop/abc.jpg 자원(사진)을 직접 응답
        //EX2) 1. http GET /images/abc.png ━▶ 2. C:/shop/abc.png 자원(사진)을 직접 응답
    }
}
