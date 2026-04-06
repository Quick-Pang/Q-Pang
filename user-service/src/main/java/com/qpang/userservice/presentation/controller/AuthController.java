package com.qpang.userservice.presentation.controller;

import com.qpang.common.response.APIResponse;
import com.qpang.common.security.JwtUtil;
import com.qpang.userservice.application.dto.user.UserInfo;
import com.qpang.userservice.application.service.AuthService;
import com.qpang.userservice.presentation.dto.auth.LoginRequest;
import com.qpang.userservice.presentation.dto.auth.SignupRequest;
import com.qpang.userservice.presentation.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 1. 회원가입 (ALL)
    @PostMapping("/signup")
    public ResponseEntity<APIResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        UserInfo userInfo = authService.signup(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(UserResponse.from(userInfo)));
    }

    // 2. 로그인 (APPROVED)
    @PostMapping("/login")
    public ResponseEntity<APIResponse<UserResponse>> login(@Valid @RequestBody LoginRequest request) {
        UserInfo userInfo = authService.login(request.toCommand());
        String token = jwtUtil.createToken(userInfo.username(), userInfo.role());

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(APIResponse.success(UserResponse.from(userInfo)));
    }

    // 3. 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<String>> refreshToken() {
        // TODO: 리프레시 토큰 로직 구현 필요 (Redis 등 상태 관리 전제)
        return ResponseEntity.ok(APIResponse.success("토큰이 갱신되었습니다. (구현 예정)"));
    }

    // 4. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout() {
        // TODO: Redis 토큰 무효화(블랙리스트) 등 로직
        return ResponseEntity.ok(APIResponse.success("로그아웃 되었습니다."));
    }
}

