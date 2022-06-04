package com.spring.jwt.jwt_sample.auth;

import com.spring.jwt.jwt_sample.model.Users;
import com.spring.jwt.jwt_sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login 요청 시 loadUserByUsername()이 호출되나, 지금은 폼로그인 disable()로 막아둠
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users userEntity = userRepository.findByUsername(username);
        System.out.println(userEntity);
        return new PrincipalDetails(userEntity);
    }
}
