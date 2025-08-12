package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //order by의 order랑 안겹치게
@Getter
@Setter
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //Many:Order  One:Member, default->즉시로딩
    @JoinColumn(name = "member_id")
    private Member member;

    // Order 조회 시 OrderItem이 같이 조회 되도록 -> OneToMany 사용 -> 읽기전용(DB에서는 사용 불가/JPA만 사용함)
    //                       ┌> OrderItem에서 Order의 필드명(OrderItem > Order > order) -> 양방향 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //-> One:Order  Many:OrderItem (클래스명) default->지연로딩


    List<OrderItem> orderItems = new ArrayList<>(); // OneToMany의 필드 타입 -> 리스트 사용

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime regTime;
    private LocalDateTime updateTime;

}
