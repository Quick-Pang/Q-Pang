package com.qpang.userservice.presentation.controller;

import com.qpang.common.response.APIResponse;
import com.qpang.userservice.application.dto.user.UserInfo;
import com.qpang.userservice.application.service.AuthService;
import com.qpang.userservice.application.service.UserService;
import com.qpang.userservice.presentation.dto.auth.RoleUpdateRequest;
import com.qpang.userservice.presentation.dto.user.UserResponse;
import com.qpang.userservice.presentation.dto.user.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 1. 내 정보 조회 (인증 서버나 게이트웨이에서 X-User-Id 헤더를 통해 전달 가정)
    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserResponse>> getMyInfo(@RequestHeader("X-User-Id") UUID userId) {
        UserInfo userInfo = userService.getUser(userId);
        return ResponseEntity.ok(APIResponse.success(UserResponse.from(userInfo)));
    }

    // 2. 사용자 목록 조회 (MASTER 권한 전용으로 Secuirty 에서 처리됨)
    @GetMapping
    public ResponseEntity<APIResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        Page<UserResponse> responses = userService.getAllUsers(pageable)
                .map(UserResponse::from);
        return ResponseEntity.ok(APIResponse.success(responses));
    }

    // 3. 단건 조회 (본인 또는 MASTER)
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponse>> getUser(@PathVariable UUID id) {
        UserInfo userInfo = userService.getUser(id);
        return ResponseEntity.ok(APIResponse.success(UserResponse.from(userInfo)));
    }

    // 4. 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserInfo userInfo = userService.updateUser(id, request.toCommand());
        return ResponseEntity.ok(APIResponse.success(UserResponse.from(userInfo)));
    }

    // 5. 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @RequestHeader("X-Deleted-By") Long deletedBy) {
        userService.deleteUser(id, deletedBy);
        return ResponseEntity.noContent().build();
    }

    // 6. 허브 소속 사용자 목록 조회 (MASTER, HUB_MANAGER)
    @GetMapping("/hub/{hubId}")
    public ResponseEntity<APIResponse<Page<UserResponse>>> getUsersByHubId(
            @PathVariable UUID hubId,
            Pageable pageable) {
        Page<UserResponse> responses = userService.getUsersByHubId(hubId, pageable)
                .map(UserResponse::from);
        return ResponseEntity.ok(APIResponse.success(responses));
    }

    // 7. 가입 대기 목록 (MASTER)
    @GetMapping("/pending")
    public ResponseEntity<APIResponse<Page<UserResponse>>> getPendingUsers(Pageable pageable) {
        Page<UserResponse> pendingUsers = authService.getPendingUsers(pageable)
                .map(UserResponse::from);
        return ResponseEntity.ok(APIResponse.success(pendingUsers));
    }

    // 8-1. 가입 승인 (MASTER)
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable UUID id) {
        userService.approveUser(id);
        return ResponseEntity.noContent().build();
    }

    // 8-2. 가입 거절 (MASTER)
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectUser(@PathVariable UUID id) {
        userService.rejectUser(id);
        return ResponseEntity.noContent().build();
    }

    // 9. 권한 수정 (MASTER)
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleUpdateRequest request) {
        userService.updateRole(id, request.role());
        return ResponseEntity.noContent().build();
    }
}

