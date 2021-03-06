package com.spring.springcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.springcore.dto.KakaoUserInfoDto;
import com.spring.springcore.model.UserRoleEnum;
import com.spring.springcore.model.Users;
import com.spring.springcore.repository.UserRepository;
import com.spring.springcore.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;//WebSecurityConfig에서 생성한 PasswordEncoder 빈이 들어오는 것
    private final UserRepository userRepository;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KakaoUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, HttpHeaders httpHeaders,
                            RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.httpHeaders = httpHeaders;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }



    public void kakaoLogin(String code) throws JsonProcessingException {
        // 리팩토링 함수화 하기.

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
       Users kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
        forceLoginUser(kakaoUser);

    }
    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = httpHeaders;
        headers.clear();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "22edaae439989644b90224c7ecfbdf11");
        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = httpHeaders;
        headers.clear();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);

        return new KakaoUserInfoDto(id, nickname, email);
    }

    private Users registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        String email = kakaoUserInfo.getEmail();
        Users kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        if (kakaoUser == null) {
            kakaoUser = userRepository.findByEmail(email)
                    .orElse(null);

            if(kakaoUser == null){
                // 회원가입
                // username: kakao nickname
                //String nickname = kakaoUserInfo.getNickname();

                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                //String email = kakaoUserInfo.getEmail();

                // email의 ID 부분 + 도메인의 맨 앞, 맨 뒤 값 붙여줘서 닉네임 만들기
                String nickname = makeUserNameForKakao(email);

                // role: 일반 사용자
                UserRoleEnum role = UserRoleEnum.USER;

                kakaoUser = new Users(nickname, encodedPassword, email, role, kakaoId);
                userRepository.save(kakaoUser);
            }else{
                // 카카오 id만 추가하기.
                kakaoUser.setKakaoId(kakaoId);
                userRepository.save(kakaoUser);
            }
        }
        return kakaoUser;
    }

    private String makeUserNameForKakao(String email) {
        String[] emailArr = email.split("@");
        String emailId = emailArr[0];
        String[] domainArr = emailArr[1].split("\\.");
        String domainVal = Character.toString(domainArr[0].charAt(0))
                + Character.toString(domainArr[0].charAt(domainArr[0].length()-1));

        return emailId + domainVal;
    }

    private void forceLoginUser(Users kakaoUser) {
        // UserDetail 생성하고 UsernamePasswordAuthenticationToken 생성한 뒤, SecurityContextHolder에서 컨텍스트를 불러와 setAuthentication해준다.
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}