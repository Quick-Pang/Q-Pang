package com.qpang.hub.infrastructure;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {

    @Override
    Page<Hub> findAll(Pageable pageable);

    List<Hub> findAllByHubType(HubType hubType);

    List<Hub> findAllByCenterHub(Hub centerHub);
}