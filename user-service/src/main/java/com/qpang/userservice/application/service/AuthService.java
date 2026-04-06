package com.qpang.userservice.application.service;

import com.qpang.common.entity.UserStatus;
import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.common.security.JwtUtil;
import com.qpang.common.service.RedisService;
import com.qpang.userservice.application.dto.auth.AuthTokenPair;
import com.qpang.userservice.application.dto.auth.LoginCommand;
import com.qpang.userservice.application.dto.auth.SignupCommand;
import com.qpang.userservice.application.dto.user.UserInfo;
import com.qpang.userservice.infrastructure.external.company.CompanyClient;
import com.qpang.userservice.infrastructure.external.company.CompanyResponseDTO;
import com.qpang.userservice.infrastructure.external.hub.HubClient;
import com.qpang.userservice.domain.entity.User;
import com.qpang.userservice.domain.repository.UserRepository;
import com.qpang.userservice.exception.UserErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:";
    private static final String ACCESS_TOKEN_BLACKLIST_KEY_PREFIX = "auth:blacklist:";
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofMillis(JwtUtil.REFRESH_TOKEN_VALID_TIME);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final HubClient hubClient;
    private final CompanyClient companyClient;

    /**
     * [회원가입]
     * 1. username 및 email 중복 검증
     * 2. password 암호화
     * 3. 엔티티 자체 매핑을 통해 PENDING 상태의 Role별 하위 엔티티 생성 후 저장
     */
    public UserInfo signup(SignupCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
        }

        validateExternalReferences(command);

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = command.toEntity(encodedPassword);

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return UserInfo.from(savedUser);
        } catch (DataIntegrityViolationException e) {
            if (userRepository.existsByUsername(command.username())) {
                throw new CustomException(UserErrorCode.DUPLICATE_USERNAME);
            }
            if (userRepository.existsByEmail(command.email())) {
                throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
            }
            throw e;
        }
    }

    /**
     * [로그인]
     * 1. 사용자 조회 (username)
     * 2. password 일치 여부 확인
     * 3. APPROVED 상태인지 확인
     * 4. access / refresh token 발급 및 refresh token Redis 저장
     */
    public AuthTokenPair login(LoginCommand command) {
        User user = userRepository.findActiveByUsername(command.username())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INVALID_PASSWORD);
        }

        if (user.getStatus() != UserStatus.APPROVED) {
            throw new CustomException(UserErrorCode.NOT_APPROVED_USER);
        }

        return issueTokenPair(user);
    }

    /**
     * [토큰 재발급]
     * refresh token이 Redis에 저장된 값과 일치할 때만 access token을 다시 발급합니다.
     */
    public AuthTokenPair refreshToken(String refreshTokenValue) {
        String refreshToken = resolveToken(refreshTokenValue);
        if (!StringUtils.hasText(refreshToken) || !jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
        String storedRefreshToken = getRefreshToken(username)
                .orElseThrow(() -> new CustomException(CommonErrorCode.UNAUTHORIZED));

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        return issueTokenPair(user);
    }

    /**
     * [로그아웃]
     * access token은 blacklist에 넣고, refresh token은 삭제합니다.
     */
    public void logout(String authorizationHeader, String refreshTokenValue) {
        boolean hasAccessToken = StringUtils.hasText(authorizationHeader);
        boolean hasRefreshToken = StringUtils.hasText(refreshTokenValue);

        if (!hasAccessToken && !hasRefreshToken) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }

        if (hasAccessToken) {
            String accessToken = resolveToken(authorizationHeader);
            if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
                blacklistAccessToken(accessToken);
                String username = jwtUtil.getUserInfoFromToken(accessToken).getSubject();
                deleteRefreshToken(username);
            }
        }

        if (hasRefreshToken) {
            String refreshToken = resolveToken(refreshTokenValue);
            if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
                String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
                deleteRefreshToken(username);
            }
        }
    }

    /**
     * [가입 승인 대기 목록 조회]
     * PENDING 상태의 사용자 목록 반환
     */
    @Transactional(readOnly = true)
    public Page<UserInfo> getPendingUsers(Pageable pageable) {
        return userRepository.findAllPendingUsers(pageable)
                .map(UserInfo::from);
    }

    private AuthTokenPair issueTokenPair(User user) {
        String accessToken = jwtUtil.createToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername(), user.getRole());
        redisService.setWithTTL(refreshTokenKey(user.getUsername()), refreshToken, REFRESH_TOKEN_TTL);

        return AuthTokenPair.builder()
                .userInfo(UserInfo.from(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Optional<String> getRefreshToken(String username) {
        return redisService.get(refreshTokenKey(username)).map(Object::toString);
    }

    private void deleteRefreshToken(String username) {
        redisService.delete(refreshTokenKey(username));
    }

    private void blacklistAccessToken(String accessToken) {
        long ttlMillis = jwtUtil.getExpirationFromToken(accessToken).getTime() - System.currentTimeMillis();
        if (ttlMillis > 0) {
            redisService.setWithTTL(accessTokenBlacklistKey(accessToken), "blacklisted", Duration.ofMillis(ttlMillis));
        }
    }

    private void validateExternalReferences(SignupCommand command) {
        switch (command.role()) {
            case MASTER -> {
                return;
            }
            case HUB_MANAGER, DELIVERY_MANAGER -> validateHub(command.hubId());
            case SUPPLIER_MANAGER -> validateCompany(command.companyId());
        }
    }

    private void validateHub(java.util.UUID hubId) {
        if (hubId == null) {
            throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
        }

        try {
            hubClient.getHubById(hubId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(UserErrorCode.HUB_NOT_FOUND);
        } catch (FeignException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateCompany(java.util.UUID companyId) {
        if (companyId == null) {
            throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
        }

        try {
            CompanyResponseDTO company = companyClient.getCompanyById(companyId);
            if (company == null
                    || !"SUPPLIER".equals(company.type())
                    || !"OPEN".equals(company.status())) {
                throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
            }
        } catch (FeignException.NotFound e) {
            throw new CustomException(UserErrorCode.COMPANY_NOT_FOUND);
        } catch (FeignException e) {
            throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String resolveToken(String tokenValue) {
        if (!StringUtils.hasText(tokenValue)) {
            return null;
        }
        return jwtUtil.substringToken(tokenValue);
    }

    private String refreshTokenKey(String username) {
        return REFRESH_TOKEN_KEY_PREFIX + username;
    }

    private String accessTokenBlacklistKey(String accessToken) {
        return ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
    }
}
