package com.shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

//AccessDeniedHandler
//기본적으로 인증된(Authenticated) 유저가 권한이 없는(Fobidden) 자원을 요청했을 때 동작
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    //권한 처리 = Access Deny( 403 )
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<body>");
        writer.println("<h1>페이지 접근 권한이 없습니다.</h1>");
        writer.println("</body>");
        writer.println("</html>");
    }


}
