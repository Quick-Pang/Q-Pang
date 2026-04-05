package com.qpang.hub.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubType {
    CENTER("중앙 허브"),
    NORMAL("일반 허브");

    private final String description;
}