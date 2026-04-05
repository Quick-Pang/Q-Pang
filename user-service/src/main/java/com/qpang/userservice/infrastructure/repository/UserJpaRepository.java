package com.qpang.userservice.infrastructure.repository;

import com.qpang.userservice.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // 승인되지 않은 사용자만 목록으로 뽑는 쿼리.
    @Query("SELECT u FROM User u WHERE u.status = 'PENDING'")
    Page<User> findAllPendingUsers(Pageable pageable);

    // 허브 매니저와 배송 관리자만 허브 기준으로 필터링.
    @Query("SELECT u FROM User u WHERE TYPE(u) IN (HubManagerUser, DeliveryManagerUser) " +
           "AND (TREAT(u AS HubManagerUser).hubId = :hubId OR TREAT(u AS DeliveryManagerUser).hubId = :hubId)")
    Page<User> findAllByHubId(@Param("hubId") UUID hubId, Pageable pageable);

}
