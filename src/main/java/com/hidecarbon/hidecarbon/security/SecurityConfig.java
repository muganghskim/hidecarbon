package com.hidecarbon.hidecarbon.security;

import com.hidecarbon.hidecarbon.user.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

//    private final AuthenticationProvider authenticationProvider;

    private final CustomOAuth2UserService customOAuth2UserService;

    private JwtConfiguration jwtConfig;



    public SecurityConfig(UserDetailsService userDetailsService, JwtConfiguration jwtConfig, CustomOAuth2UserService customOAuth2UserService
                          ) {
        this.userDetailsService = userDetailsService;
//        this.authenticationProvider = authenticationProvider;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtConfig = jwtConfig;

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // You need to define passwordEncoder() method
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(authenticationManager(), jwtConfig, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/**"))
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        //todo: 밑에 주석 제거
//                        .requestMatchers(("/admin/**")).hasRole("A")
                        .requestMatchers(
                                ("/v3/**"),
                                ("/swagger-ui/**"),
                                ("/user/**"),
                                ("/admin/**"),
                                ("/user/updateToken"),
                                ("/chatbot/**"),
                                ("/charge/**"),
                                ("/data/**"),
                                ("/error"),
                                ("/advanceDeposit/**"),
                                ("/template/**"),
                                ("/sub/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                ).oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/loginSuccess")  // 로그인 성공 후 리다이렉트할 URL
                        .failureUrl("/loginFailure")             // 로그인 실패 후 리다이렉트할 URL
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
//                        .userInfoEndpoint()                       // 사용자 정보를 가져올 때의 설정들을 담당합니다.
//                        .userService(customOAuth2UserService)    // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체 등록
                );
//                        .requestMatchers(new AntPathRequestMatcher("/api/v1/**")).hasRole(Member.)
//                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

}
