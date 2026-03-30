package com.qpang.hub.application.service;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    public Page<Hub> getAllHubs(Pageable pageable) {
        return hubRepository.findAll(pageable);
    }
}