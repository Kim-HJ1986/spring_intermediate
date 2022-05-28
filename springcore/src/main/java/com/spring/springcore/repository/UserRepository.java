package com.spring.springcore.repository;

import com.spring.springcore.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByKakaoId(Long kakaoId);
    Optional<Users> findByEmail(String email);
}
