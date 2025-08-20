package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepogitory;
import com.shop.repository.MemberRepository;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepogitory itemImgRepogitory;

    public Long order(OrderDto orderDto, String email) {
        // 1. orderDto >> itemId -> item 정보 조회
        Item item = itemRepository.findById(orderDto.getItemId())
                                  .orElseThrow(EntityNotFoundException::new);
        // 2. email -> member 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        // 3. OrderItem 엔티티 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        // 4. Order 엔티티 생성 => Order 엔티티 >> orderItems(List)에 OrderItem 엔티티 추가
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        // 5. Order 엔티티 저장 -> OrderItem 엔티티 저장
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrders(email);

        List<OrderHistDto> orderHistDtoList = new ArrayList<>();
        for (Order order : orders) { //주문내역 하나씩
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) { //주문내역 상세 내용
                ItemImg itemImg = itemImgRepogitory.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtoList.add(orderHistDto);
        }
        return new PageImpl<>(orderHistDtoList, pageable, totalCount);
    }

    public void cancelOrder(Long orderId) {
        //orderId가 같은 Order 엔티티를 조회해서 cancelOrder() 호출
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Boolean validateOrder(Long orderId, String email) {
        // orderId로 조회된 Order 엔티티의 member.email과 매개변수로 받은 email이 같은지 체크(확인)
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        return StringUtils.equals(email, order.getMember().getEmail());
    }

    public Long orders(List<OrderDto> orderDtoList, String email){
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                                      .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        // 5. Order 엔티티 저장 -> OrderItem 엔티티 저장
        orderRepository.save(order);
        return order.getId();
    }
}
