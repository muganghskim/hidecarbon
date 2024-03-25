package com.hidecarbon.hidecarbon.security;

import com.hidecarbon.hidecarbon.user.model.Member;
import com.hidecarbon.hidecarbon.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final JwtConfiguration jwtConfiguration;
    private final UserRepository userRepository;

    private final UserDetailsService userDetailsService;

    public JwtProvider(JwtConfiguration jwtConfiguration, UserRepository userRepository, UserDetailsService userDetailsService) {
        this.jwtConfiguration = jwtConfiguration;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(Authentication authentication) {
        String userEmail = authentication.getName();
        Optional<Member> user = userRepository.findByUserEmail(userEmail);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (user == null) {
            throw new UsernameNotFoundException("User with userEmail: " + userEmail + " not found");
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime tokenExpiration = now.plusSeconds(jwtConfiguration.getTokenValidity());


        return Jwts.builder()
                .setSubject(userEmail)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenExpiration.toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtConfiguration.getSecret())
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        String userEmail = authentication.getName();
        Optional<Member> user = userRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User with userEmail: " + userEmail + " not found");
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime refreshTokenExpiration = now.plusSeconds(jwtConfiguration.getReftokenValidity());

        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(refreshTokenExpiration.toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtConfiguration.getRefSecret())
                .compact();
    }

    public String updateAccessToken(String refreshToken) {
        try {
            // 리프레시 토큰 확인
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtConfiguration.getRefSecret()).parseClaimsJws(refreshToken);

            // 해당 사용자에 대한 새로운 액세스 토큰 생성
            String cmpId = claims.getBody().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(cmpId);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = generateToken(authentication);

            return newAccessToken;
        } catch (Exception e) {
            log.error("new access token exception: {}", e);
            return null;
        }
    }
}
