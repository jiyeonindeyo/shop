package com.shop.controller;

import com.shop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller//뷰 이름 리턴(String) -> 뷰 이름 찾기
@RequestMapping("/thymeleaf")
public class ThymeleafExController {

    @GetMapping("/ex01") //            ┌> data를 뷰로 전달(뷰랑 컨트롤 이어줌)
    public String thymeleafExample01(Model model) {
        model.addAttribute("data", "Thymeleaf 예제입니다.");
        return "thymeleafEx/thymeleafEx01"; //뷰 이름 리턴 -> 리턴받은 뷰 이름(위치)찾음
    }

    @GetMapping("/ex02")
    public String thymeleafExample02(Model model) {
        ItemDto itemDto = new ItemDto();
        itemDto.setItemDetail("상품상세설명");
        itemDto.setItemName("상품2");
        itemDto.setPrice(20_000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto", itemDto);
        return "thymeleafEx/thymeleafEx02";
    }

    @GetMapping("/ex03")
    public String thymeleafExample03(Model model) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemName("테스트 상품" + i);
            itemDto.setPrice(10_000 + i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList", itemDtoList);
        return "thymeleafEx/thymeleafEx03";
    }

    @GetMapping("/ex04")
    public String thymeleafExample04(Model model) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명" + i);
            itemDto.setItemName("테스트 상품" + i);
            itemDto.setPrice(10_000 + i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList", itemDtoList);
        return "thymeleafEx/thymeleafEx04";
    }

    @GetMapping("/ex05")
    public String thymeleafExample05(Model model) {
        String param1 = "스프링 부트";
        String param2 = "타임리프";
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "thymeleafEx/thymeleafEx05";
    }

    @GetMapping("/ex06")
    public String thymeleafExample06(@RequestParam String param1,
                                     @RequestParam String param2,
                                     Model model) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @GetMapping("/ex07")
    public String thymeleafExample07() {
        return "thymeleafEx/thymeleafEx07";
    }
}
