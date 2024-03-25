package com.hidecarbon.hidecarbon.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class JwtConfiguration {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.refSecret}")
    private  String refSecret;
    @Value("${jwt.tokenValidity}")
    private long tokenValidity;
    @Value("${jwt.reftokenValidity}")
    private long reftokenValidity;
}

