package com.spring.springcore.repository;

import com.spring.springcore.model.ApiUseTime;
import com.spring.springcore.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(Users user);
}
