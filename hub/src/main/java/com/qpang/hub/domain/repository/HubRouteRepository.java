package com.qpang.hub.domain.repository;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRouteRepository {

    HubRoute save(HubRoute hubRoute);

    Optional<HubRoute> findById(UUID id);

    Page<HubRoute> findAll(Pageable pageable);

    List<HubRoute> findBySourceHub(Hub sourceHub);

    void deleteByHub(Hub hub);

    List<HubRoute> findBySourceHubToCenter(Hub sourceHub);

    Optional<HubRoute> findBySourceHubAndDestinationHub(Hub sourceHub, Hub destinationHub);
}