package com.qpang.deliverymanager.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerType {
    HUB("허브 배송 담당자"),
    COMPANY("업체 배송 담당자");

    private final String description;
}