package com.spring.jwt.jwt_sample.controller;

import com.spring.jwt.jwt_sample.auth.PrincipalDetails;
import com.spring.jwt.jwt_sample.model.Users;
import com.spring.jwt.jwt_sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RestApiController {
    public final BCryptPasswordEncoder bCryptPasswordEncoder;
    public final UserRepository userRepository;

    // 모든 사람이 접근 가능
    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    // Tip : JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가능.
    // 왜냐하면 @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문이다.
    // 놉!! 리턴 해주고있다. 따라서 @AuthenticationPrincipal 사용 가능하다!

    @PostMapping("/join")
    public String join(@RequestBody Users user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);
        return "회원가입완료";
    }

    // user, manager, admin 권한 접근 가능
    @GetMapping("/api/v1/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails, Authentication authentication){
        System.out.println("principal : "+principalDetails.getUser().getId());
        System.out.println("principal : "+principalDetails.getUser().getUsername());
        System.out.println("principal : "+principalDetails.getUser().getPassword());

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal : "+principal.getUser().getId());
        System.out.println("principal : "+principal.getUser().getUsername());
        System.out.println("principal : "+principal.getUser().getPassword());

        return "<h1>user</h1>";
    }

    // manager, admin 권한 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager(){
        return "manager";
    }

    // admin 권한 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin(){
        return "admin";
    }
}
