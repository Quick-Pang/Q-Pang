package com.qpang.hub.domain.repository;

import com.qpang.hub.domain.model.Hub;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {
    Hub save(Hub hub);
    Optional<Hub> findById(UUID id);
    List<Hub> findAll();
    void delete(Hub hub);
}