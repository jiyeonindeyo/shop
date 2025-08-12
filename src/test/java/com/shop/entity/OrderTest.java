package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10_000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        //1. 주문 엔티티 생성
        Order order = new Order();
        //orderRepository.save(order);
        // item 기준정보 생성, 기준정보로 주문-아이템 생성
        for (int i=0; i<3; i++) {
            //2. 아이템 엔티티 저장
            Item item = createItem();
            itemRepository.save(item);
            //3. 위에서 저장된 아이템으로 주문-아이템 저장(주문과 특정 아이템 연결해주는 역할)
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); //FK
            orderItem.setOrder(order); //FK
            orderItem.setCount(10);
            orderItem.setOrderPrice(1_000);
            //orderRepository.save(orderItem);
            // 현재 Order 저장 X, order 테이블에 orderItems 리스트에 orderItem 엔티티를 추가
            order.getOrderItems().add(orderItem);
        }
        // 현재 Order는 저장 전 OrderItem 엔티티를 3개 포함한 상태
        orderRepository.saveAndFlush(order);
        em.clear(); //영속성 컨텍스트 비우기 -> 이후 사용하면 저장된 캐시없어서 SQL쿼리가 날아감

        Order savedOrder = orderRepository.findById(order.getId())
                                          .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());

    }

    public Order createOrder() {
        //1. 주문 엔티티 생성
        Order order = new Order();
        //orderRepository.save(order);
        // item 기준정보 생성, 기준정보로 주문-아이템 생성
        for (int i=0; i<3; i++) {
            //2. 아이템 엔티티 저장
            Item item = createItem();
            itemRepository.save(item);
            //3. 위에서 저장된 아이템으로 주문-아이템 저장(주문과 특정 아이템 연결해주는 역할)
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); //FK
            orderItem.setOrder(order); //FK
            orderItem.setCount(10);
            orderItem.setOrderPrice(1_000);
            //orderRepository.save(orderItem);
            // 현재 Order 저장 X, order 테이블에 orderItems 리스트에 orderItem 엔티티를 추가
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아 객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        Order order = createOrder();
        OrderItem orderItem = order.getOrderItems().get(0); //->0번째 orderitem
        Long orderItemId = orderItem.getId();

        //영속성 context 삭제
        em.flush();
        em.clear();

        OrderItem savedOrderItem = orderItemRepository.findById(orderItemId)
                                                      .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class: " + savedOrderItem.getOrder().getClass());
        System.out.println("==============================");
        savedOrderItem.getOrder().getOrderDate();
        System.out.println("==============================");
        savedOrderItem.getItem().getItemDetail();
    }

}