package com.qpang.userservice.infrastructure.repository;

import com.qpang.userservice.domain.entity.User;
import com.qpang.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        // 실제 저장은 Spring Data JPA 구현체에 위임함.
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAllPendingUsers(Pageable pageable) {
        // 승인 대기자 전용 JPQL 조회를 그대로 연결한다.
        return userJpaRepository.findAllPendingUsers(pageable);
    }

    @Override
    public Page<User> findAllByHubId(UUID hubId, Pageable pageable) {
        // 허브 소속 사용자 조회 쿼리를 인프라 계층에서 감싼다.
        return userJpaRepository.findAllByHubId(hubId, pageable);
    }
}
