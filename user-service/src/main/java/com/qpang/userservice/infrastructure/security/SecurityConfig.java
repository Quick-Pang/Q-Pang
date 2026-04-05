package com.qpang.userservice.infrastructure.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 세션 대신 JWT를 쓰므로 CSRF와 세션 생성을 끄는 코드.
        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers("/auth/signup", "/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 관리자 전용 사용자 처리 API는 MASTER 역할만 접근한다.
                        .requestMatchers("/users/{id}/approve", "/users/{id}/reject", "/users/pending", "/users/{id}/role").hasRole("MASTER")
                        // 허브 관련 조회는 허브 담당 역할까지 허용한다.
                        .requestMatchers("/users/hub/**").hasAnyRole("MASTER", "HUB_MANAGER")
                        // 전체 사용자 조회는 최고 관리자만 허용한다.
                        .requestMatchers("/users").hasRole("MASTER")
                        .anyRequest().authenticated()
        );

        // 요청마다 JWT를 검사해서 SecurityContext를 채우는코드.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
