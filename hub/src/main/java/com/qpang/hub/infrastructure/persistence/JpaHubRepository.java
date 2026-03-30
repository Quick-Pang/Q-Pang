package com.qpang.hub.infrastructure.persistence;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.repository.HubRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaHubRepository extends JpaRepository<Hub, UUID>, HubRepository {
}