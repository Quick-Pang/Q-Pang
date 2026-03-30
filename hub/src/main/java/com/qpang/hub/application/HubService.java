package com.qpang.hub.application.service;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    public List<Hub> getAllHubs() {
        return hubRepository.findAll();
    }
}