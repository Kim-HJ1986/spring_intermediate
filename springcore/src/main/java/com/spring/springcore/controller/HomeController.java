package com.spring.springcore.controller;

import com.spring.springcore.model.UserRoleEnum;
import com.spring.springcore.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    // 컨트롤러의 파라미터 자리에서 @AuthenticationPrincipal을 붙여준 UserDetailsImpl userDetailsImpl 로 받을 수 있다.
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username", userDetails.getUsername());

        if (userDetails.getUser().getRole() == UserRoleEnum.ADMIN) {
            model.addAttribute("admin_role", true);
        }

        return "index";
    }
}
