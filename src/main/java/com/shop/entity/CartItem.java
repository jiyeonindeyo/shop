package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
public class CartItem extends BaseTimeEntity {

    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Many:CartItm(현재 클래스), One:Cart(필드 클래스)
    @JoinColumn(name = "cart_id") //join할 컬럼명이 똑같은지 확인!
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY) //Many:CartItm, One:Item
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer count; //필드에 적을땐 랩퍼타입으로 함(원시 타입X) -> int(X) Integer(O)

}
