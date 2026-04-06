package com.qpang.userservice.application.service;

import com.qpang.common.exception.CustomException;
import com.qpang.userservice.application.dto.auth.LoginCommand;
import com.qpang.userservice.application.dto.auth.SignupCommand;
import com.qpang.userservice.application.dto.user.UserInfo;
import com.qpang.userservice.domain.entity.User;
import com.qpang.userservice.domain.repository.UserRepository;
import com.qpang.userservice.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [회원가입]
     * 1. username 및 email 중복 검증
     * 2. password 암호화
     * 3. 엔티티 자체 매핑을 통해 PENDING 상태의 Role별 하위 엔티티 생성 후 저장
     */
    public UserInfo signup(SignupCommand command) {
        // 사용자 식별값이 이미 존재하면 먼저 차단한다.
        if (userRepository.existsByUsername(command.username())) {
            throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
        }
        // 이메일도 유니크 조건이므로 중복 가입을 막는다.
        if (userRepository.existsByEmail(command.email())) {
            throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호는 평문 저장하지 않고 해시로 저장한다.
        String encodedPassword = passwordEncoder.encode(command.password());
        User user = command.toEntity(encodedPassword);
        
        // 가입 직후에는 아직 승인 전 상태로 저장한다.
        User savedUser = userRepository.save(user);
        return UserInfo.from(savedUser);
    }

    /**
     * [로그인]
     * 1. 사용자 조회 (username)
     * 2. password 일치 여부 확인
     * 3. APPROVED 상태인지 확인 (엔티티의 역할이어야 하지만 상태 체크만 서비스에서 수행)
     * return: 인증된 UserInfo 반환 (주로 AuthenticationFilter/JWT 에 의해 활용)
     */
    @Transactional(readOnly = true)
    public UserInfo login(LoginCommand command) {
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        // 입력한 비밀번호가 저장된 해시와 일치하는지 확인한다.
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INVALID_PASSWORD);
        }

        // 승인된 사용자만 로그인할 수 있도록 막는다.
        if (user.getStatus() != com.qpang.common.entity.UserStatus.APPROVED) {
            throw new CustomException(UserErrorCode.NOT_APPROVED_USER);
        }

        return UserInfo.from(user);
    }

    /**
     * [가입 승인 대기 목록 조회]
     * PENDING 상태의 사용자 목록 반환
     */
    @Transactional(readOnly = true)
    public Page<UserInfo> getPendingUsers(Pageable pageable) {
        // 승인 대기 상태만 조회해서 화면에 전달한다.
        return userRepository.findAllPendingUsers(pageable)
                .map(UserInfo::from);
    }

    // Refresh Token 관련 로직은 JWT 모듈 설계에 따라 확장 필요
}
