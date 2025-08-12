package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // AuditorAware ━▶ 엔티티 생성 및 수정 시, 해당 행위의 주체(유저) 정보를 알아내는 역할
        // 구현 : SecurityContext의 Authentication 내에 다양한 유저 정보 有(:쓰레드 로컬에 위치) -> 유저 아이디 return
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String username = "";
        if (authentication != null) {
            username = authentication.getName();
        }
        return Optional.of(username);
    }
}
