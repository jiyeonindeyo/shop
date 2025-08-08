package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // controller의 리턴은 String
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/new") //┌>(생략가능)여러값이 한 번에 날라올 경우 알아서 매핑됨(따로 set해줄 필요X)
    public String memberForm(@ModelAttribute @Valid MemberFormDto memberFormDto,
            // MemberFormDto에서 유효성 검사 적용 <┘ ┌>뷰까지 자동 전달 됨(모델에 담지 않아도 가능!) *@Valid 명시한 뒤 작성해야 유효함*
                                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
                //└> form 제출 후 페이지 이동할수있도록(redirect:)
    }

    @GetMapping("/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "이메일주소 혹은 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

}
