package com.spring.jwt.jwt_sample.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.spring.jwt.jwt_sample.auth.PrincipalDetails;
import com.spring.jwt.jwt_sample.model.Users;
import com.spring.jwt.jwt_sample.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 시큐리티가 filter를 가지고 있는데, 그 필터중에 BasicAuthenticationFilter 라는 것이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 경우 위 필터를 무조건 타게 되어있다.
// 만약에 권한이나 인증이 필요한 주소가 아니라면 이 필터를 타지 않는다. -> 아닌 것 같음 모든 요청을 검토하는 것 같다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 인증이나 권한이 필요한 주소 요청이 있을 때 해당 필터를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이 옴");

        // header가 있는지 확인
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING);
        System.out.println("jwtHeader: " + jwtHeader);
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)){
            chain.doFilter(request, response);
            return;
        }

        // JWT 검증을 통해 정상적인 사용자인지 확인
        String jwtToken = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX, "");
        String username =
                JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwtToken).getClaim("username").asString();

        //서명이 제대로 됐다면!
        if(username != null){
            Users userEntity = userRepository.findByUsername(username);
            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            // Authentication객체를 만들어준다
            //임의로 authentication 객체를 생성해줌 (username이 null이 아니므로 (정상 유저이므로 ) credentail = null로 넣어도 됨)
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
