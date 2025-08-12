package com.shop.entity;

import com.shop.dto.MemberFormDto;

import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("hello@test.com");
        memberFormDto.setName("hello");
        memberFormDto.setAddress("창원시");
        memberFormDto.setPassword("12345678");
        return Member.createMember(memberFormDto,  passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
        // 1.신규 회원 등록
        Member member = createMember();
        memberRepository.save(member);
        // 2.해당 회원 엔티티의 장바구니 생성 및 등록
        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // DB를 사용해서 값 호출하기 위해 DB저장, 캐시삭제
        em.flush(); //DB에 저장시키고(SQL 지연 저장소에 있는것들 DB로 보냄)
        em.clear(); //cache 메모리 삭제
        // └> 굳이 DB로 들고와서 사용할 필요X (테스트를 위해 확인차 사용) -> 캐시 사용이 더 나음!

        System.out.println("저장하기 전 cart의 주소값: " + cart.hashCode());
        System.out.println("저장하기 전 member의 주소값: " + member.hashCode());

        // 3.저장된 장바구니를 통해 해당 회원 조회
        Optional<Cart> savedCartOp = cartRepository.findById(cart.getId());
        // 3-1. Optional 안에 값이 존재하면 꺼내서 받고, null 이면 EntityNotFoundException 던지기
        Cart savedCart = savedCartOp.orElseThrow(EntityNotFoundException::new);
        // 4.조회된 회원과저장한 회원 정보가 일치하는지 확인
         /*                  (( DB 관점 ))
         * 1) cart 테이블 조회, 2) 조회된 cart 테이블에서 member_id FK값을 얻는다.
         * 3) 얻은 FK로 member 테이블에서 연고나된 유저를 찾는다
         *                        or
         * 1) cart 테이블과 member 테이블 사이에 member_id FK를 조건으로 JOIN한다. */
        // 4-1. 회원꺼내기 (↓ JPA(객체)관점 ↓)
        Member foundMember = savedCart.getMember();
        assertEquals(foundMember.getId(), member.getId());

        System.out.println("저장한 후 cart의 주소값: " + savedCart.hashCode());
        System.out.println("저장한 후 member의 주소값: " + foundMember.hashCode());
    }


}
