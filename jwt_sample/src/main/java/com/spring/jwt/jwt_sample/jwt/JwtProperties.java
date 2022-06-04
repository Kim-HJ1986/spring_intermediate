package com.spring.jwt.jwt_sample.jwt;

public interface JwtProperties {
    String SECRET = "secret_key";
    int EXPIRATION_TIME = 60000*10;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

}
