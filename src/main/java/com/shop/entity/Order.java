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
public class Order extends BaseTimeEntity {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Many:Order  One:Member, default->즉시로딩
    @JoinColumn(name = "member_id")
    private Member member;

    // Order 조회 시 OrderItem이 같이 조회 되도록 -> OneToMany 사용 -> 읽기전용(DB에서는 사용 불가/JPA만 사용함)
    //                       ┌> OrderItem에서 Order의 필드명(OrderItem > Order > order) -> 양방향 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY) //-> One:Order  Many:OrderItem (클래스명)
                                                                // └> default : 지연로딩 (명시적으로 작성했음)

    List<OrderItem> orderItems = new ArrayList<>(); // OneToMany의 필드 타입 -> 리스트 사용

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    //BaseTimeEntity 상속으로 동일한 필드명 존재하므로 삭제
//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

    private void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
