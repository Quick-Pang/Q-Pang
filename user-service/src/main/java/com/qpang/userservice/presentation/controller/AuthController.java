package com.qpang.userservice.presentation.controller;

import com.qpang.common.response.APIResponse;
import com.qpang.common.security.JwtUtil;
import com.qpang.userservice.application.dto.auth.AuthTokenPair;
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
        AuthTokenPair authTokenPair = authService.login(request.toCommand());

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, authTokenPair.accessToken());
        headers.set("X-Refresh-Token", authTokenPair.refreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(APIResponse.success(UserResponse.from(authTokenPair.userInfo())));
    }

    // 3. 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<UserResponse>> refreshToken(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        AuthTokenPair authTokenPair = authService.refreshToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, authTokenPair.accessToken());
        headers.set("X-Refresh-Token", authTokenPair.refreshToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(APIResponse.success(UserResponse.from(authTokenPair.userInfo())));
    }

    // 4. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout(
            @RequestHeader(value = JwtUtil.AUTHORIZATION_HEADER, required = false) String authorizationHeader,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        authService.logout(authorizationHeader, refreshToken);
        return ResponseEntity.ok(APIResponse.success("로그아웃 되었습니다."));
    }
}

