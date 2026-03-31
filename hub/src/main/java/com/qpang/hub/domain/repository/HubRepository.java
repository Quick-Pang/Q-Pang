package com.qpang.hub.domain.repository;

import com.qpang.hub.domain.model.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // import 확인!
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {
    Hub save(Hub hub);
    Optional<Hub> findById(UUID id);
    Page<Hub> findAll(Pageable pageable);
    //void delete(Hub hub);
}