package com.qpang.hub.domain.repository;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {

    Hub save(Hub hub);

    Optional<Hub> findById(UUID id);

    Page<Hub> findAll(Pageable pageable);

    List<Hub> findAll();

    long count();

    List<Hub> findAllByHubType(HubType hubType);

    List<Hub> findAllByCenterHub(Hub centerHub);
}