package com.qpang.hub.infrastructure;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubRoute;
import com.qpang.hub.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepository {

    private final HubRouteJpaRepository hubRouteJpaRepository;

    @Override
    public HubRoute save(HubRoute hubRoute) {
        return hubRouteJpaRepository.save(hubRoute);
    }

    @Override
    public Optional<HubRoute> findById(UUID id) {
        return hubRouteJpaRepository.findById(id);
    }

    @Override
    public Page<HubRoute> findAll(Pageable pageable) {
        return hubRouteJpaRepository.findAll(pageable);
    }

    @Override
    public List<HubRoute> findBySourceHub(Hub sourceHub) {
        return hubRouteJpaRepository.findBySourceHub(sourceHub);
    }

    @Override
    public void deleteByHub(Hub hub) {
        hubRouteJpaRepository.deleteByHub(hub);
    }

    @Override
    public List<HubRoute> findBySourceHubToCenter(Hub sourceHub) {
        return hubRouteJpaRepository.findBySourceHubToCenter(sourceHub);
    }

    @Override
    public Optional<HubRoute> findBySourceHubAndDestinationHub(Hub sourceHub, Hub destinationHub) {
        return hubRouteJpaRepository.findBySourceHubAndDestinationHub(sourceHub, destinationHub);
    }
}