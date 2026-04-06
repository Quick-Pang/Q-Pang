package com.qpang.userservice.infrastructure.security;

import com.qpang.common.security.JwtUtil;
import com.qpang.common.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/auth/signup",
            "/auth/login",
            "/auth/refresh"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 인증이 필요 없는 경로는 JWT 검증을 건너뜁니다.
        String path = request.getServletPath();
        return EXCLUDE_PATHS.stream().anyMatch(path::equals);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 토큰을 꺼내 인증 정보를 복원하는 코드.
        String tokenValue = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(tokenValue)) {
            String token = jwtUtil.substringToken(tokenValue);

            if (token != null && !redisService.hasKey(blacklistKey(token))) {
                try {
                    Claims info = jwtUtil.getUserInfoFromToken(token);
                    // 토큰의 subject와 role로 SecurityContext를 채우는코드.
                    setAuthentication(info.getSubject(), (String) info.get(JwtUtil.AUTHORIZATION_KEY));
                } catch (JwtException | IllegalArgumentException e) {
                    log.debug("Invalid JWT token");
                }
            } else if (token != null) {
                log.debug("Blacklisted JWT token");
            }
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username, String role) {
        // Spring Security가 읽을 수 있는 인증 객체를 저장한다.
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username, role);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username, String role) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        // 실제 인증은 토큰 기반이므로 비밀번호는 비워둔 UserDetails를 사용한다.
        UserDetails userDetails = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String blacklistKey(String token) {
        return "auth:blacklist:" + token;
    }
}
