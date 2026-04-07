package com.qpang.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        // 로컬/dev에서는 감사자 값을 강제로 채우지 않는다.
        return Optional.empty();
    }
}
