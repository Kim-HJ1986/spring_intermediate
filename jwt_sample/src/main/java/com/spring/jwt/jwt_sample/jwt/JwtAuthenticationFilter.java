package com.spring.jwt.jwt_sample.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.jwt_sample.auth.PrincipalDetails;
import com.spring.jwt.jwt_sample.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에 UsernamePasswordAuthenticationFilter가 있음
// /login 요청해서 username, password 전송하면(post)
// UsernamePasswordAuthenticationFilter가 동작을 함.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중..");

        // 1. username, password 받아서
        // ** request.getInputStream() -> 클라이언트에서 보낸 request의 값이 담겨있다.
        // 아래의 방식으로 request에 담긴 정보를 직접적으로 추출할 수 있다.
        try {
            //JSON 형식으로 받았을 때 읽어오는 방법. -> @RequestBody와 같은 기능!
            ObjectMapper om = new ObjectMapper();
            Users user = om.readValue(request.getInputStream(), Users.class);
            System.out.println(user);

            //폼 로그인 이었으면 토큰은 자동으로 생성된다. 하지만 JSON 형식으로 받아왔기 때문에 토큰을 만들어줘야 한다.
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // PrincipalDetailsService의 loadUserByUsername() 함수가 실행되어
            // PrincipleDetails에 찾아온 user를 넣어 Authentication으로 리턴해줌. -> Authentication 생성
            // Authentication으로 리턴하는 과정에서 user 객체를 DB에서 조회해오면, authenticationManager가 알아서 비밀번호까지 검증해준다.
            // 즉 아래 코드가 정상적으로 작동하면 DB에 있는 username과 password가 일치한다는 뜻이다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 정상적인 authentication -> getPrincipal()로 가져온 것이 출력이 된다면 로그인 성공한 것!!
//            PrincipalDetails principalDetails = (PrincipalDetails) authenticationToken.getPrincipal();
//            System.out.println("로그인 완료됨: " + principalDetails.getUser().getUsername());

            // authentication 리턴 시 SESSION에 저장됨.
            // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는 것!
            // 굳이 JWT를 사용하며 세션을 만들 이유가 없다. 단지 권한 처리때문에 session에 넣어줌
            // setDetails() 안해줘도 잘 나온다!! 낚시 였어
            //setDetails(request, authenticationToken);
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행된다.
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 로그인 및 인증이 완료되었다는 뜻");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA가 아닌 Hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject("토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
