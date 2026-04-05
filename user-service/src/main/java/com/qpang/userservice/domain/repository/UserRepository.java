package com.qpang.userservice.domain.repository;

import com.qpang.userservice.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllPendingUsers(Pageable pageable);

    Page<User> findAllByHubId(UUID hubId, Pageable pageable);
}
