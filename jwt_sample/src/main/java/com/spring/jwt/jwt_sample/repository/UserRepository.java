package com.spring.jwt.jwt_sample.repository;

import com.spring.jwt.jwt_sample.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
}
