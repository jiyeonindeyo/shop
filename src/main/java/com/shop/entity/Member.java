package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
    // hi
    @Id //PK
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column //컬럼명이랑 변수명이랑 이름이 같을 경우 @Column 생략가능
    private String name;
    @Column(unique = true) //email 중복 방지
    private String email;
    private String password;
    private String address;

    @Enumerated(EnumType.STRING) //저장 방법(문자열로 저장)
    private Role role; //유저 권한

    //Member는 Bean이 아닌 Entity라서 passwordEncoder를 따로 받아서 사용 가능 <┐
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());

        String encodedPw = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(encodedPw);
        member.setRole(Role.USER);
        return member;
    }
}
