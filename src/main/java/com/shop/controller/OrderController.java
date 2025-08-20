package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.exception.OutOfStockException;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //비동기 처리
    @PostMapping("/order")
    @ResponseBody //return값 그대로를 데이터 형식으로 응답
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,
                                          BindingResult bindingResult,
                                          Authentication authentication) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getField())
                  .append(" : ")
                  .append(fieldError.getDefaultMessage())
                  .append("\n");
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); //400
        }
        Long orderId = null;
        try {
            orderId = orderService.order(orderDto, authentication.getName());
        } catch (OutOfStockException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(orderId);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable Optional<Integer> page,
                            Authentication authentication,
                            Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(authentication.getName(), pageable);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/orders/{orderId}/cancel")
     //     ┌> return 타입으로 ResponseEntity를 사용하면 @ResponseBody 생략해도 괜찮음
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                         Authentication authentication) {
        if (!orderService.validateOrder(orderId, authentication.getName())) {
            // 내 주문이 아닌데 주문하려고 한 경우 -> 주문취소 권한X
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.",
                                                    HttpStatus.FORBIDDEN); // 403
        }
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().body(orderId);
    }

}



