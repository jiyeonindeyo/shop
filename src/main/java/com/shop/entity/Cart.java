package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // member 테이블의 member_id(PK)를 참조하는 cart 테이블의 member_id(FK)
    // └> private Long member_id -> 이렇게 표현X   부모 테이블의 컬럼명과 일치 해야함 <──┐
    // One To One -> 앞의 one과 뒤의 one을 구분해주어야함 / unique                    ㅣ
    @OneToOne(fetch = FetchType.LAZY) // fetch->즉시로딩 ↔ lazy->지연로딩            │
    @JoinColumn(name = "member_id") //@JoinColumn에 들어가는 name -> FK 컬럼이름 ────┘
    private Member member;

}
