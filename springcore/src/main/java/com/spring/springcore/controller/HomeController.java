package com.spring.springcore.controller;

import com.spring.springcore.model.Folder;
import com.spring.springcore.model.UserRoleEnum;
import com.spring.springcore.repository.FolderRepository;
import com.spring.springcore.security.UserDetailsImpl;
import com.spring.springcore.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final FolderService folderService;

    @GetMapping("/")
    // 컨트롤러의 파라미터 자리에서 @AuthenticationPrincipal을 붙여준 UserDetailsImpl userDetailsImpl 로 받을 수 있다.
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("username", userDetails.getUsername());

        if (userDetails.getUser().getRole() == UserRoleEnum.ADMIN) {
            model.addAttribute("admin_role", true);
        }

        List<Folder> folderList = folderService.getFolders(userDetails.getUser());
        model.addAttribute("folders", folderList);

        return "index";
    }
}
