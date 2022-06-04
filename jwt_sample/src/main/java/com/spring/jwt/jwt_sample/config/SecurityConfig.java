package com.spring.jwt.jwt_sample.config;

import com.spring.jwt.jwt_sample.jwt.JwtAuthenticationFilter;
import com.spring.jwt.jwt_sample.jwt.JwtAuthorizationFilter;
import com.spring.jwt.jwt_sample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;

    @Override
    public void configure(WebSecurity web){
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // 시큐리티 필터가 실행되기 전에 필터 적용 -> JWT 필터 넣으며 주석처리함.
//        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class);
        http
                .csrf().disable()
                // 모든 요청을 허용한다. CORS 정책에서 벗어날 수 있음, 크로스 오리진 요청이 와도 다 허용된다. (인증이 필요한 요청도 포함!!)
                .addFilter(corsConfig.corsFilter()) // @CrossOrigin는 인증이 필요 없는 Controller 메서드 위에 붙여서 사용한다.
                // 현재 아래 생성자에 넣어줄 authenticationManager() 때문에 WebSecurityConfigurerAdapter 상속받음 -> 수정 필요!
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // /login 호출 시 로그인 하도록 필터 넣어줌. 단, 생성자 파라미터로 AuthenticationManager를 던져줘야한다.
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) // 인증이 필요한 요청이 올 경우 해당 필터가 실행됨. 단, 생성자 파라미터로 AuthenticationManager를 던져줘야한다.
                // 아래 값들은 JWT에서 거의 고정값임.
                // 세션 생성을 막아준다. (세션을 사용하지 않겠다.) -> 세션 사용했는데..
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable() // form태그로 로그인 안한다.
                .httpBasic().disable()// httpBasic 방식은 Authorization에 ID,PW를 들고 다니는 방식이다. <-> Bearer 방식 (토큰을 들고다니는 방식)
                .authorizeRequests()
                        .antMatchers("/api/v1/user/**")
                        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                        .antMatchers("/api/v1/manager/**")
                        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                        .antMatchers("/api/v1/admin/**")
                        .access("hasRole('ROLE_ADMIN')")
                        .anyRequest().permitAll();


    }

}
