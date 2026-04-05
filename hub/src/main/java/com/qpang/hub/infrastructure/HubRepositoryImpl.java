package com.qpang.hub.infrastructure;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubType;
import com.qpang.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

    private final HubJpaRepository hubJpaRepository;

    @Override
    public Hub save(Hub hub) {
        return hubJpaRepository.save(hub);
    }

    @Override
    public Optional<Hub> findById(UUID id) {
        return hubJpaRepository.findById(id);
    }

    @Override
    public Page<Hub> findAll(Pageable pageable) {
        return hubJpaRepository.findAll(pageable);
    }

    @Override
    public List<Hub> findAll() {
        return hubJpaRepository.findAll();
    }

    @Override
    public long count() {
        return hubJpaRepository.count();
    }

    @Override
    public List<Hub> findAllByHubType(HubType hubType) {
        return hubJpaRepository.findAllByHubType(hubType);
    }

    @Override
    public List<Hub> findAllByCenterHub(Hub centerHub) {
        return hubJpaRepository.findAllByCenterHub(centerHub);
    }
}