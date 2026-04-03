package com.qpang.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        // 테스트용 고정 UUID
        return Optional.of(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }
}