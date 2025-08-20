package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.cartItem.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.item.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional //한개당 하나의 트랜잭션 (트랜잭션없음 dirty checking안됨 -> 업데이트 비정상적)
@RequiredArgsConstructor
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto,
                        String email) {
        // 목적 : CartItem을 저장
        // 1. Item 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 2. Member 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        // 3. Cart 엔티티 조회 -> 없다 -> Cart 저장
        Cart cart = cartRepository.findByMember(member);
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        // 4. CartItem 조회 -> 없다 -> CartItem 엔티티 저장
        //                 -> 있다 -> 조회된 CartItem 엔티티 count 증가
        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cart, item);
        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId(); // 5. CartItem => 저장
        } else {
            CartItem newCartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(newCartItem);
            return newCartItem.getId(); // 5. CartItem => 저장
        }
    };

    public List<CartDetailDto> getCartItems(String email) {
        // 1. Member 조회
        Member member = memberRepository.findByEmail(email);
        // 2. Cart 조회
        Cart cart = cartRepository.findByMember(member);
        if (cart == null) {
            return new ArrayList<>();
        }
        // 3. CartItem 조회 => 특정 Cart에 들어있는 CartItem 리스트 조회
        return cartItemRepository.findCartDetailDtoList(cart.getId());
    }

    public void updateCartItemCount(Long cartItemId, Integer count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        // 방법1
//        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
//        cartItemRepository.delete(cartItem);
        // 방법2
        cartItemRepository.deleteById(cartItemId);
    }

    public Long orderCartItems(List<CartOrderDto> cartOrderDtoList, String email) {
        // 주문 → 수행 → orderService => OrderDtoList, email
        // 책임1 : CartItemDto에 있는 cartItemId를 이용 -> orderDtoList를 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                                                  .orElseThrow(EntityNotFoundException::new);
            OrderDto orderDto = OrderDto.builder()
                                        .itemId(cartItem.getItem().getId())
                                        .count(cartItem.getCount())
                                        .build();
            orderDtoList.add(orderDto);
        }
        Long orderId = orderService.orders(orderDtoList, email);
        // 책임2 : 주문이 완료된 CartItem을 테이블에서 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            cartItemRepository.deleteById(cartOrderDto.getCartItemId());
        }
        return orderId;
    }
}
