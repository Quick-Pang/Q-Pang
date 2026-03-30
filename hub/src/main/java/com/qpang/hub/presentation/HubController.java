package com.qpang.hub.presentation.controller;

import com.qpang.hub.application.service.HubService;
import com.qpang.hub.domain.model.Hub;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    @GetMapping
    public List<Hub> getAllHubs() {
        return hubService.getAllHubs();
    }
}