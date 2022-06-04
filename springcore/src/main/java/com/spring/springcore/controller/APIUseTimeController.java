package com.spring.springcore.controller;

import com.spring.springcore.model.ApiUseTime;
import com.spring.springcore.model.UserRoleEnum;
import com.spring.springcore.repository.ApiUseTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class APIUseTimeController {
    private final ApiUseTimeRepository apiUseTimeRepository;

    @Secured(UserRoleEnum.Authority.ADMIN)
    @GetMapping("/api/use/time")
    public List<ApiUseTime> getAllApiUseTime(){
        return apiUseTimeRepository.findAll();
    }
}
